package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.RecurringTransactionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.AccountType
import com.example.data.model.InsightItem
import com.example.data.model.InsightType
import com.example.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar

data class AccountBalance(
    val account: AccountEntity,
    val currentBalanceMinorUnits: Long
)

data class BudgetProgress(
    val budget: BudgetEntity,
    val spentMinorUnits: Long,
    val remainingMinorUnits: Long,
    val progressPercentage: Float, // 0.0 to 1.0+
    val daysRemainingInMonth: Int,
    val dailySpendingAllowanceMinorUnits: Long
)

class FinanceRepository(private val database: AppDatabase) {

    private val accountDao = database.accountDao()
    private val categoryDao = database.categoryDao()
    private val transactionDao = database.transactionDao()
    private val budgetDao = database.budgetDao()
    private val savingsGoalDao = database.savingsGoalDao()
    private val recurringDao = database.recurringTransactionDao()

    val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val recentTransactions: Flow<List<TransactionEntity>> = transactionDao.getRecentTransactions(5)
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()
    val allGoals: Flow<List<SavingsGoalEntity>> = savingsGoalDao.getAllGoals()
    val allRecurring: Flow<List<RecurringTransactionEntity>> = recurringDao.getAllRecurring()
    val activeRecurring: Flow<List<RecurringTransactionEntity>> = recurringDao.getActiveRecurring()

    // Derived Account Balances (Source of truth derived from transactions + initialBalance)
    val accountBalances: Flow<List<AccountBalance>> = combine(allAccounts, allTransactions) { accounts, transactions ->
        accounts.map { account ->
            var balance = account.initialBalanceMinorUnits
            for (tx in transactions) {
                if (tx.accountId == account.id) {
                    when (tx.type) {
                        TransactionType.EXPENSE.name -> balance -= tx.amountMinorUnits
                        TransactionType.INCOME.name -> balance += tx.amountMinorUnits
                        TransactionType.TRANSFER.name -> balance -= tx.amountMinorUnits
                    }
                }
                if (tx.toAccountId == account.id && tx.type == TransactionType.TRANSFER.name) {
                    balance += tx.amountMinorUnits
                }
            }
            AccountBalance(account, balance)
        }
    }

    // Monthly totals: Income & Expenses for current calendar month
    val currentMonthTotals: Flow<Pair<Long, Long>> = allTransactions.map { transactions ->
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH)

        var monthIncome = 0L
        var monthExpense = 0L

        val txCal = Calendar.getInstance()
        for (tx in transactions) {
            txCal.timeInMillis = tx.dateMillis
            if (txCal.get(Calendar.YEAR) == currentYear && txCal.get(Calendar.MONTH) == currentMonth) {
                when (tx.type) {
                    TransactionType.INCOME.name -> monthIncome += tx.amountMinorUnits
                    TransactionType.EXPENSE.name -> monthExpense += tx.amountMinorUnits
                }
            }
        }
        Pair(monthIncome, monthExpense)
    }

    // Budget progress calculations for current month
    val budgetProgressList: Flow<List<BudgetProgress>> = combine(allBudgets, allTransactions) { budgets, transactions ->
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val totalDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val daysRemaining = (totalDaysInMonth - currentDay + 1).coerceAtLeast(1)

        val txCal = Calendar.getInstance()
        val monthExpenses = transactions.filter { tx ->
            if (tx.type != TransactionType.EXPENSE.name) return@filter false
            txCal.timeInMillis = tx.dateMillis
            txCal.get(Calendar.YEAR) == currentYear && txCal.get(Calendar.MONTH) == currentMonth
        }

        budgets.map { budget ->
            val spent = monthExpenses.filter { tx ->
                budget.categoryId == null || tx.categoryId == budget.categoryId
            }.sumOf { it.amountMinorUnits }

            val remaining = (budget.limitMinorUnits - spent).coerceAtLeast(0L)
            val progress = if (budget.limitMinorUnits > 0) {
                (spent.toFloat() / budget.limitMinorUnits.toFloat()).coerceAtLeast(0f)
            } else 0f

            val dailyAllowance = if (daysRemaining > 0) remaining / daysRemaining else 0L

            BudgetProgress(
                budget = budget,
                spentMinorUnits = spent,
                remainingMinorUnits = remaining,
                progressPercentage = progress,
                daysRemainingInMonth = daysRemaining,
                dailySpendingAllowanceMinorUnits = dailyAllowance
            )
        }
    }

    // Rule-based Local Insights
    val localInsights: Flow<List<InsightItem>> = combine(allTransactions, allBudgets, allCategories) { txs, budgets, cats ->
        val insights = mutableListOf<InsightItem>()
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH)

        // Split current month vs previous month expenses
        val currentMonthExpenses = mutableListOf<TransactionEntity>()
        val prevMonthExpenses = mutableListOf<TransactionEntity>()

        val txCal = Calendar.getInstance()
        for (tx in txs) {
            if (tx.type != TransactionType.EXPENSE.name) continue
            txCal.timeInMillis = tx.dateMillis
            val y = txCal.get(Calendar.YEAR)
            val m = txCal.get(Calendar.MONTH)
            if (y == currentYear && m == currentMonth) {
                currentMonthExpenses.add(tx)
            } else if ((y == currentYear && m == currentMonth - 1) || (m == 11 && currentMonth == 0 && y == currentYear - 1)) {
                prevMonthExpenses.add(tx)
            }
        }

        val currentTotal = currentMonthExpenses.sumOf { it.amountMinorUnits }
        val prevTotal = prevMonthExpenses.sumOf { it.amountMinorUnits }

        // Comparison insight
        if (prevTotal > 0 && currentTotal > 0) {
            val diff = ((currentTotal - prevTotal).toDouble() / prevTotal.toDouble() * 100).toInt()
            if (diff > 5) {
                insights.add(
                    InsightItem(
                        title = "Monthly Spending Alert",
                        description = "Your spending is $diff% higher than at this point last month.",
                        type = InsightType.WARNING,
                        iconName = "trending_up"
                    )
                )
            } else if (diff < -5) {
                insights.add(
                    InsightItem(
                        title = "Great Savings Pace",
                        description = "You are spending ${-diff}% less than last month. Keep up the good momentum!",
                        type = InsightType.POSITIVE,
                        iconName = "savings"
                    )
                )
            }
        }

        // Top category insight
        if (currentMonthExpenses.isNotEmpty()) {
            val byCategory = currentMonthExpenses.groupBy { it.categoryId }
            val topEntry = byCategory.maxByOrNull { entry -> entry.value.sumOf { it.amountMinorUnits } }
            if (topEntry != null) {
                val categoryName = cats.find { it.id == topEntry.key }?.name ?: "General"
                val topSum = topEntry.value.sumOf { it.amountMinorUnits }
                val percentOfTotal = if (currentTotal > 0) (topSum * 100 / currentTotal).toInt() else 0
                insights.add(
                    InsightItem(
                        title = "Top Spending Category",
                        description = "$categoryName is your largest expense this month ($percentOfTotal% of expenses).",
                        type = InsightType.NEUTRAL,
                        iconName = "pie_chart"
                    )
                )
            }
        }

        if (insights.isEmpty()) {
            insights.add(
                InsightItem(
                    title = "Privacy First Finance",
                    description = "All financial records and analytics are computed 100% locally on your device.",
                    type = InsightType.POSITIVE,
                    iconName = "lock"
                )
            )
        }

        insights
    }

    // CRUD operations
    suspend fun insertAccount(account: AccountEntity): Long = accountDao.insertAccount(account)
    suspend fun updateAccount(account: AccountEntity) = accountDao.updateAccount(account)
    suspend fun deleteAccount(account: AccountEntity) = accountDao.deleteAccount(account)

    suspend fun insertCategory(category: CategoryEntity): Long = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    suspend fun insertTransaction(transaction: TransactionEntity): Long = transactionDao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = transactionDao.deleteTransaction(transaction)
    suspend fun deleteTransactionById(id: Long) = transactionDao.deleteTransactionById(id)

    suspend fun insertBudget(budget: BudgetEntity): Long = budgetDao.insertBudget(budget)
    suspend fun updateBudget(budget: BudgetEntity) = budgetDao.updateBudget(budget)
    suspend fun deleteBudget(budget: BudgetEntity) = budgetDao.deleteBudget(budget)
    suspend fun deleteBudgetById(id: Long) = budgetDao.deleteBudgetById(id)

    suspend fun insertGoal(goal: SavingsGoalEntity): Long = savingsGoalDao.insertGoal(goal)
    suspend fun updateGoal(goal: SavingsGoalEntity) = savingsGoalDao.updateGoal(goal)
    suspend fun deleteGoal(goal: SavingsGoalEntity) = savingsGoalDao.deleteGoal(goal)
    suspend fun deleteGoalById(id: Long) = savingsGoalDao.deleteGoalById(id)

    suspend fun addMoneyToGoal(goalId: Long, amountMinorUnits: Long) {
        val goal = savingsGoalDao.getGoalById(goalId) ?: return
        val updated = goal.copy(currentMinorUnits = goal.currentMinorUnits + amountMinorUnits)
        savingsGoalDao.updateGoal(updated)
    }

    suspend fun withdrawMoneyFromGoal(goalId: Long, amountMinorUnits: Long) {
        val goal = savingsGoalDao.getGoalById(goalId) ?: return
        val updated = goal.copy(currentMinorUnits = (goal.currentMinorUnits - amountMinorUnits).coerceAtLeast(0L))
        savingsGoalDao.updateGoal(updated)
    }

    suspend fun insertRecurring(recurring: RecurringTransactionEntity): Long = recurringDao.insertRecurring(recurring)
    suspend fun updateRecurring(recurring: RecurringTransactionEntity) = recurringDao.updateRecurring(recurring)
    suspend fun deleteRecurring(recurring: RecurringTransactionEntity) = recurringDao.deleteRecurring(recurring)
    suspend fun deleteRecurringById(id: Long) = recurringDao.deleteRecurringById(id)
}
