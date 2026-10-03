package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.RecurringTransactionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.AccountType
import com.example.data.model.Frequency
import com.example.data.model.InsightItem
import com.example.data.model.TransactionType
import com.example.data.repository.AccountBalance
import com.example.data.repository.BackupMetadata
import com.example.data.repository.BackupRestoreManager
import com.example.data.repository.BudgetProgress
import com.example.data.repository.FinanceRepository
import com.example.data.repository.SecurityManager
import com.example.data.repository.UserPreferences
import com.example.data.repository.UserPreferencesRepository
import com.example.notifications.NotificationPreferences
import com.example.notifications.NotificationPreferencesRepository
import com.example.notifications.NotificationProcessor
import com.example.notifications.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

data class TransactionsFilterState(
    val searchQuery: String = "",
    val typeFilter: String? = null, // null = all, EXPENSE, INCOME, TRANSFER
    val categoryIdFilter: Long? = null,
    val accountIdFilter: Long? = null
)

class FinanceViewModel(
    private val financeRepository: FinanceRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val backupRestoreManager: BackupRestoreManager,
    private val notificationPreferencesRepository: NotificationPreferencesRepository,
    private val appContext: Context
) : ViewModel() {

    // User preferences & settings
    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    // Notification preferences stream
    val notificationPreferences: StateFlow<NotificationPreferences> = notificationPreferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotificationPreferences()
        )

    // Data streams
    val accounts: StateFlow<List<AccountEntity>> = financeRepository.allAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accountBalances: StateFlow<List<AccountBalance>> = financeRepository.accountBalances
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = financeRepository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = financeRepository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<TransactionEntity>> = financeRepository.recentTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentMonthTotals: StateFlow<Pair<Long, Long>> = financeRepository.currentMonthTotals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(0L, 0L))

    val budgetProgressList: StateFlow<List<BudgetProgress>> = financeRepository.budgetProgressList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savingsGoals: StateFlow<List<SavingsGoalEntity>> = financeRepository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recurringTransactions: StateFlow<List<RecurringTransactionEntity>> = financeRepository.allRecurring
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val localInsights: StateFlow<List<InsightItem>> = financeRepository.localInsights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App Lock state (transient session unlocked flag)
    val isAppUnlocked = MutableStateFlow(false)

    // Transactions Screen Filter State
    val filterState = MutableStateFlow(TransactionsFilterState())

    // Filtered transactions stream
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        filterState
    ) { txList, filter ->
        txList.filter { tx ->
            val matchesSearch = filter.searchQuery.isBlank() || tx.note.contains(filter.searchQuery, ignoreCase = true)
            val matchesType = filter.typeFilter == null || tx.type == filter.typeFilter
            val matchesCat = filter.categoryIdFilter == null || tx.categoryId == filter.categoryIdFilter
            val matchesAcc = filter.accountIdFilter == null || tx.accountId == filter.accountIdFilter || tx.toAccountId == filter.accountIdFilter

            matchesSearch && matchesType && matchesCat && matchesAcc
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Total balance derived across all accounts
    val totalBalanceMinorUnits: StateFlow<Long> = accountBalances.combine(userPreferences) { balances, _ ->
        balances.sumOf { it.currentBalanceMinorUnits }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Filter updates
    fun updateSearchQuery(query: String) {
        filterState.value = filterState.value.copy(searchQuery = query)
    }

    fun updateTypeFilter(type: String?) {
        filterState.value = filterState.value.copy(typeFilter = type)
    }

    fun updateCategoryFilter(categoryId: Long?) {
        filterState.value = filterState.value.copy(categoryIdFilter = categoryId)
    }

    fun updateAccountFilter(accountId: Long?) {
        filterState.value = filterState.value.copy(accountIdFilter = accountId)
    }

    fun clearFilters() {
        filterState.value = TransactionsFilterState()
    }

    // App lock logic
    fun unlockWithPin(pin: String): Boolean {
        val storedHash = userPreferences.value.pinHash
        val verified = SecurityManager.verifyPin(pin, storedHash)
        if (verified) {
            isAppUnlocked.value = true
        }
        return verified
    }

    fun unlockWithBiometrics() {
        isAppUnlocked.value = true
    }

    fun lockApp() {
        if (userPreferences.value.isAppLockEnabled) {
            isAppUnlocked.value = false
        }
    }

    // Onboarding completion
    fun completeOnboarding(
        currencyCode: String,
        enableLock: Boolean,
        pin: String,
        useBiometrics: Boolean,
        initialAccountName: String? = "Cash",
        initialBalanceMinorUnits: Long = 0L
    ) {
        viewModelScope.launch {
            preferencesRepository.setDefaultCurrency(currencyCode)
            if (enableLock && pin.isNotBlank()) {
                val hash = SecurityManager.hashPin(pin)
                preferencesRepository.setAppLock(true, hash, useBiometrics)
            }
            // Create initial account if provided and none exists
            if (!initialAccountName.isNullOrBlank() && accounts.value.isEmpty()) {
                val acc = AccountEntity(
                    name = initialAccountName,
                    type = AccountType.CASH.name,
                    currencyCode = currencyCode,
                    initialBalanceMinorUnits = initialBalanceMinorUnits,
                    colorHex = "#7CB9E8",
                    iconName = "account_balance_wallet"
                )
                financeRepository.insertAccount(acc)
            }
            preferencesRepository.setOnboardingCompleted(true)
            isAppUnlocked.value = true
        }
    }

    // Settings update
    fun updateDefaultCurrency(currencyCode: String) {
        viewModelScope.launch {
            preferencesRepository.setDefaultCurrency(currencyCode)
        }
    }

    fun updateThemeMode(mode: com.example.ui.theme.AppThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun updateAppLock(enabled: Boolean, pin: String, useBiometrics: Boolean) {
        viewModelScope.launch {
            val hash = if (pin.isNotBlank()) SecurityManager.hashPin(pin) else ""
            preferencesRepository.setAppLock(enabled, hash, useBiometrics)
        }
    }

    // Transaction actions
    fun addTransaction(
        type: TransactionType,
        amountMinorUnits: Long,
        currencyCode: String,
        categoryId: Long?,
        accountId: Long,
        toAccountId: Long?,
        dateMillis: Long,
        note: String,
        isRecurring: Boolean = false,
        recurringFrequency: Frequency? = null
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                type = type.name,
                amountMinorUnits = amountMinorUnits,
                currencyCode = currencyCode,
                categoryId = categoryId,
                accountId = accountId,
                toAccountId = toAccountId,
                dateMillis = dateMillis,
                note = note,
                isRecurring = isRecurring
            )
            financeRepository.insertTransaction(tx)
            NotificationProcessor.processBudgetThresholdAlerts(appContext)

            // Update smart recent defaults
            preferencesRepository.updateLastUsed(
                categoryId = categoryId ?: -1L,
                accountId = accountId,
                type = type.name
            )

            // If user checked "Make recurring", automatically register recurring schedule
            if (isRecurring && recurringFrequency != null) {
                val nextDue = calculateNextDueDate(dateMillis, recurringFrequency)
                val recurring = RecurringTransactionEntity(
                    title = note.ifBlank { "Recurring ${type.name.lowercase().replaceFirstChar { it.uppercase() }}" },
                    type = type.name,
                    amountMinorUnits = amountMinorUnits,
                    currencyCode = currencyCode,
                    categoryId = categoryId,
                    accountId = accountId,
                    toAccountId = toAccountId,
                    frequency = recurringFrequency.name,
                    nextDueDateMillis = nextDue,
                    note = note
                )
                financeRepository.insertRecurring(recurring)
                NotificationProcessor.processRecurringReminders(appContext)
            }
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            financeRepository.updateTransaction(transaction)
            NotificationProcessor.processBudgetThresholdAlerts(appContext)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            financeRepository.deleteTransaction(transaction)
            NotificationProcessor.processBudgetThresholdAlerts(appContext)
        }
    }

    // Account actions
    fun addAccount(
        name: String,
        type: AccountType,
        currencyCode: String,
        initialBalanceMinorUnits: Long,
        colorHex: String = "#7CB9E8",
        iconName: String = "account_balance"
    ) {
        viewModelScope.launch {
            val account = AccountEntity(
                name = name,
                type = type.name,
                currencyCode = currencyCode,
                initialBalanceMinorUnits = initialBalanceMinorUnits,
                colorHex = colorHex,
                iconName = iconName
            )
            financeRepository.insertAccount(account)
        }
    }

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            financeRepository.updateAccount(account)
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            financeRepository.deleteAccount(account)
        }
    }

    // Budget actions
    fun addBudget(
        name: String,
        limitMinorUnits: Long,
        currencyCode: String,
        categoryId: Long?
    ) {
        viewModelScope.launch {
            val budget = BudgetEntity(
                name = name,
                limitMinorUnits = limitMinorUnits,
                currencyCode = currencyCode,
                categoryId = categoryId
            )
            financeRepository.insertBudget(budget)
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            financeRepository.deleteBudget(budget)
        }
    }

    // Savings Goal actions
    fun addSavingsGoal(
        name: String,
        targetMinorUnits: Long,
        currentMinorUnits: Long,
        currencyCode: String,
        targetDateMillis: Long,
        colorHex: String = "#F7C59F"
    ) {
        viewModelScope.launch {
            val goal = SavingsGoalEntity(
                name = name,
                targetMinorUnits = targetMinorUnits,
                currentMinorUnits = currentMinorUnits,
                currencyCode = currencyCode,
                targetDateMillis = targetDateMillis,
                colorHex = colorHex
            )
            financeRepository.insertGoal(goal)
        }
    }

    fun addMoneyToGoal(goalId: Long, amountMinorUnits: Long) {
        viewModelScope.launch {
            financeRepository.addMoneyToGoal(goalId, amountMinorUnits)
            NotificationProcessor.processSavingsGoalReminders(appContext)
        }
    }

    fun withdrawMoneyFromGoal(goalId: Long, amountMinorUnits: Long) {
        viewModelScope.launch {
            financeRepository.withdrawMoneyFromGoal(goalId, amountMinorUnits)
        }
    }

    fun deleteSavingsGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            financeRepository.deleteGoal(goal)
        }
    }

    // Recurring actions
    fun addRecurringTransaction(
        title: String,
        type: TransactionType,
        amountMinorUnits: Long,
        currencyCode: String,
        categoryId: Long?,
        accountId: Long,
        toAccountId: Long?,
        frequency: Frequency,
        startDateMillis: Long,
        note: String
    ) {
        viewModelScope.launch {
            val nextDue = calculateNextDueDate(startDateMillis, frequency)
            val entity = RecurringTransactionEntity(
                title = title,
                type = type.name,
                amountMinorUnits = amountMinorUnits,
                currencyCode = currencyCode,
                categoryId = categoryId,
                accountId = accountId,
                toAccountId = toAccountId,
                frequency = frequency.name,
                nextDueDateMillis = nextDue,
                startDateMillis = startDateMillis,
                note = note
            )
            financeRepository.insertRecurring(entity)
        }
    }

    fun toggleRecurringActive(recurring: RecurringTransactionEntity) {
        viewModelScope.launch {
            financeRepository.updateRecurring(recurring.copy(isActive = !recurring.isActive))
        }
    }

    fun deleteRecurring(recurring: RecurringTransactionEntity) {
        viewModelScope.launch {
            financeRepository.deleteRecurring(recurring)
        }
    }

    // Category actions
    fun addCategory(name: String, type: TransactionType, iconName: String, colorHex: String) {
        viewModelScope.launch {
            val category = CategoryEntity(
                name = name,
                type = type.name,
                iconName = iconName,
                colorHex = colorHex
            )
            financeRepository.insertCategory(category)
        }
    }

    // Backup & Restore
    suspend fun exportBackupJson(): String {
        return backupRestoreManager.createBackupJson()
    }

    suspend fun saveBackupFile(): File {
        return backupRestoreManager.saveBackupToFile()
    }

    suspend fun restoreBackup(jsonString: String, replaceExisting: Boolean): Result<BackupMetadata> {
        return backupRestoreManager.restoreFromJson(jsonString, replaceExisting)
    }

    // Notification Settings Methods
    fun updateNotificationMaster(enabled: Boolean) {
        viewModelScope.launch {
            notificationPreferencesRepository.setMasterEnabled(enabled)
            val prefs = notificationPreferencesRepository.getPreferences()
            NotificationScheduler.scheduleAll(appContext, prefs)
        }
    }

    fun updateNotificationRecurring(enabled: Boolean) {
        viewModelScope.launch {
            notificationPreferencesRepository.setRecurringEnabled(enabled)
            if (enabled) NotificationScheduler.scheduleRecurringReminders(appContext)
            else NotificationScheduler.cancelRecurringReminders(appContext)
        }
    }

    fun updateNotificationBudgets(enabled: Boolean) {
        viewModelScope.launch {
            notificationPreferencesRepository.setBudgetAlertsEnabled(enabled)
            if (enabled) NotificationProcessor.processBudgetThresholdAlerts(appContext)
        }
    }

    fun updateNotificationGoals(enabled: Boolean) {
        viewModelScope.launch {
            notificationPreferencesRepository.setSavingsGoalsEnabled(enabled)
            if (enabled) NotificationProcessor.processSavingsGoalReminders(appContext)
        }
    }

    fun updateNotificationDaily(enabled: Boolean) {
        viewModelScope.launch {
            notificationPreferencesRepository.setDailySummaryEnabled(enabled)
            val prefs = notificationPreferencesRepository.getPreferences()
            if (enabled) NotificationScheduler.scheduleDailySummary(appContext, prefs.dailySummaryHour, prefs.dailySummaryMinute)
            else NotificationScheduler.cancelDailySummary(appContext)
        }
    }

    fun updateNotificationMonthly(enabled: Boolean) {
        viewModelScope.launch {
            notificationPreferencesRepository.setMonthlySummaryEnabled(enabled)
            val prefs = notificationPreferencesRepository.getPreferences()
            if (enabled) NotificationScheduler.scheduleMonthlySummary(appContext, prefs.monthlySummaryDay, prefs.monthlySummaryHour, prefs.monthlySummaryMinute)
            else NotificationScheduler.cancelMonthlySummary(appContext)
        }
    }

    fun updateDailySummaryTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            notificationPreferencesRepository.setDailySummaryTime(hour, minute)
            NotificationScheduler.scheduleDailySummary(appContext, hour, minute)
        }
    }

    fun updateMonthlySummarySchedule(day: Int, hour: Int, minute: Int) {
        viewModelScope.launch {
            notificationPreferencesRepository.setMonthlySummarySchedule(day, hour, minute)
            NotificationScheduler.scheduleMonthlySummary(appContext, day, hour, minute)
        }
    }

    fun updateBudgetThreshold(threshold: Int, enabled: Boolean) {
        viewModelScope.launch {
            when (threshold) {
                75 -> notificationPreferencesRepository.setBudgetThreshold75(enabled)
                90 -> notificationPreferencesRepository.setBudgetThreshold90(enabled)
                100 -> notificationPreferencesRepository.setBudgetThreshold100(enabled)
            }
            if (enabled) NotificationProcessor.processBudgetThresholdAlerts(appContext)
        }
    }

    fun updateNotificationPrivacy(enabled: Boolean) {
        viewModelScope.launch {
            notificationPreferencesRepository.setPrivacyMode(enabled)
        }
    }

    fun sendTestNotification() {
        viewModelScope.launch {
            NotificationProcessor.sendTestNotification(appContext)
        }
    }

    private fun calculateNextDueDate(fromMillis: Long, frequency: Frequency): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = fromMillis }
        when (frequency) {
            Frequency.DAILY -> cal.add(Calendar.DAY_OF_YEAR, 1)
            Frequency.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            Frequency.MONTHLY -> cal.add(Calendar.MONTH, 1)
            Frequency.YEARLY -> cal.add(Calendar.YEAR, 1)
        }
        return cal.timeInMillis
    }
}
