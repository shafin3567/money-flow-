package com.example.notifications

import android.content.Context
import com.example.MoneyFlowApplication
import com.example.data.model.CurrencyData
import com.example.data.model.TransactionType
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object NotificationProcessor {

    suspend fun processRecurringReminders(context: Context) {
        val app = context.applicationContext as? MoneyFlowApplication ?: return
        val prefsRepo = NotificationPreferencesRepository(context)
        val prefs = prefsRepo.getPreferences()
        if (!prefs.masterEnabled || !prefs.recurringEnabled) return

        val userPrefs = app.userPreferencesRepository.userPreferencesFlow.first()
        val defaultCurrency = userPrefs.defaultCurrency

        val activeRecurring = app.database.recurringTransactionDao().getActiveRecurring().first()
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        // Reminder window: within the next 24 hours or today
        val oneDayMillis = 24 * 60 * 60 * 1000L

        for (recurring in activeRecurring) {
            val dueDiff = recurring.nextDueDateMillis - now
            // If due within the next 24 hours or overdue within the last 12 hours
            if (dueDiff in (-12 * 60 * 60 * 1000L)..oneDayMillis) {
                val notifId = (10000 + recurring.id).toInt()
                val formattedAmount = CurrencyData.formatMoney(recurring.amountMinorUnits, recurring.currencyCode.ifBlank { defaultCurrency })

                val (title, body) = if (prefs.privacyModeEnabled) {
                    "Upcoming payment due" to "A scheduled recurring bill is due today."
                } else {
                    "Payment due today" to "${recurring.title} · $formattedAmount"
                }

                MoneyFlowNotificationManager.showNotification(
                    context = context,
                    notificationId = notifId,
                    channelId = MoneyFlowNotificationManager.CHANNEL_BILLS,
                    title = title,
                    body = body,
                    destinationSubScreen = MoneyFlowNotificationManager.DESTINATION_RECURRING,
                    actionTitle = "View Recurring"
                )
            }
        }
    }

    suspend fun processBudgetThresholdAlerts(context: Context, specificBudgetId: Long? = null) {
        val app = context.applicationContext as? MoneyFlowApplication ?: return
        val prefsRepo = NotificationPreferencesRepository(context)
        val prefs = prefsRepo.getPreferences()
        if (!prefs.masterEnabled || !prefs.budgetAlertsEnabled) return

        val userPrefs = app.userPreferencesRepository.userPreferencesFlow.first()
        val defaultCurrency = userPrefs.defaultCurrency

        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH)
        val monthStr = String.format(Locale.US, "%04d-%02d", currentYear, currentMonth + 1)

        val allBudgets = app.database.budgetDao().getAllBudgets().first()
        val allTransactions = app.database.transactionDao().getAllTransactions().first()

        val txCal = Calendar.getInstance()
        val monthExpenses = allTransactions.filter { tx ->
            if (tx.type != TransactionType.EXPENSE.name) return@filter false
            txCal.timeInMillis = tx.dateMillis
            txCal.get(Calendar.YEAR) == currentYear && txCal.get(Calendar.MONTH) == currentMonth
        }

        val budgetsToCheck = if (specificBudgetId != null) {
            allBudgets.filter { it.id == specificBudgetId }
        } else {
            allBudgets
        }

        for (budget in budgetsToCheck) {
            if (budget.limitMinorUnits <= 0) continue

            val spent = monthExpenses.filter { tx ->
                budget.categoryId == null || tx.categoryId == budget.categoryId
            }.sumOf { it.amountMinorUnits }

            val percentage = (spent.toDouble() / budget.limitMinorUnits.toDouble() * 100).toInt()
            val spentFormatted = CurrencyData.formatMoney(spent, budget.currencyCode.ifBlank { defaultCurrency })
            val limitFormatted = CurrencyData.formatMoney(budget.limitMinorUnits, budget.currencyCode.ifBlank { defaultCurrency })

            // 1. 100% Exceeded alert
            val key100 = "budget_${budget.id}_${monthStr}_100"
            if (percentage >= 100 && budget.notify100 && prefs.budgetThreshold100 && !prefs.lastTriggeredBudgetThresholds.contains(key100)) {
                val notifId = (20000 + budget.id * 10 + 3).toInt()
                val (title, body) = if (prefs.privacyModeEnabled) {
                    "${budget.name} budget exceeded" to "You have exceeded your ${budget.name} budget limit."
                } else {
                    "${budget.name} budget exceeded" to "You've spent $spentFormatted of your $limitFormatted budget."
                }

                MoneyFlowNotificationManager.showNotification(
                    context = context,
                    notificationId = notifId,
                    channelId = MoneyFlowNotificationManager.CHANNEL_BUDGET,
                    title = title,
                    body = body,
                    destinationTab = MoneyFlowNotificationManager.DESTINATION_BUDGETS,
                    actionTitle = "View Budget"
                )
                prefsRepo.recordBudgetThresholdTriggered(key100)
            }
            // 2. 90% threshold alert
            else if (percentage in 90..99 && budget.notify90 && prefs.budgetThreshold90) {
                val key90 = "budget_${budget.id}_${monthStr}_90"
                if (!prefs.lastTriggeredBudgetThresholds.contains(key90)) {
                    val notifId = (20000 + budget.id * 10 + 2).toInt()
                    val (title, body) = if (prefs.privacyModeEnabled) {
                        "${budget.name} budget is 90% used" to "You have reached 90% of your ${budget.name} budget limit."
                    } else {
                        "${budget.name} budget is 90% used" to "$spentFormatted of your $limitFormatted budget has been used."
                    }

                    MoneyFlowNotificationManager.showNotification(
                        context = context,
                        notificationId = notifId,
                        channelId = MoneyFlowNotificationManager.CHANNEL_BUDGET,
                        title = title,
                        body = body,
                        destinationTab = MoneyFlowNotificationManager.DESTINATION_BUDGETS,
                        actionTitle = "View Budget"
                    )
                    prefsRepo.recordBudgetThresholdTriggered(key90)
                }
            }
            // 3. 75% threshold alert
            else if (percentage in 75..89 && budget.notify75 && prefs.budgetThreshold75) {
                val key75 = "budget_${budget.id}_${monthStr}_75"
                if (!prefs.lastTriggeredBudgetThresholds.contains(key75)) {
                    val notifId = (20000 + budget.id * 10 + 1).toInt()
                    val (title, body) = if (prefs.privacyModeEnabled) {
                        "${budget.name} budget is 75% used" to "You have reached 75% of your ${budget.name} budget limit."
                    } else {
                        "${budget.name} budget is 75% used" to "$spentFormatted of your $limitFormatted budget has been used."
                    }

                    MoneyFlowNotificationManager.showNotification(
                        context = context,
                        notificationId = notifId,
                        channelId = MoneyFlowNotificationManager.CHANNEL_BUDGET,
                        title = title,
                        body = body,
                        destinationTab = MoneyFlowNotificationManager.DESTINATION_BUDGETS,
                        actionTitle = "View Budget"
                    )
                    prefsRepo.recordBudgetThresholdTriggered(key75)
                }
            }
        }
    }

    suspend fun processSavingsGoalReminders(context: Context) {
        val app = context.applicationContext as? MoneyFlowApplication ?: return
        val prefsRepo = NotificationPreferencesRepository(context)
        val prefs = prefsRepo.getPreferences()
        if (!prefs.masterEnabled || !prefs.savingsGoalsEnabled) return

        val userPrefs = app.userPreferencesRepository.userPreferencesFlow.first()
        val defaultCurrency = userPrefs.defaultCurrency

        val allGoals = app.database.savingsGoalDao().getAllGoals().first()

        for (goal in allGoals) {
            if (goal.isArchived || goal.targetMinorUnits <= 0) continue

            // Goal Completed (100% reached)
            if (goal.currentMinorUnits >= goal.targetMinorUnits) {
                val goalIdStr = goal.id.toString()
                if (!prefs.lastTriggeredGoalCompletedIds.contains(goalIdStr)) {
                    val notifId = (30000 + goal.id).toInt()
                    val title = "🎉 Savings goal completed"
                    val body = "You reached your target for ${goal.name}!"

                    MoneyFlowNotificationManager.showNotification(
                        context = context,
                        notificationId = notifId,
                        channelId = MoneyFlowNotificationManager.CHANNEL_SAVINGS,
                        title = title,
                        body = body,
                        destinationSubScreen = MoneyFlowNotificationManager.DESTINATION_SAVINGS_GOALS,
                        actionTitle = "View Goal"
                    )
                    prefsRepo.recordGoalCompletedTriggered(goal.id)
                }
            } else {
                // Periodic Progress update
                val percentage = ((goal.currentMinorUnits.toDouble() / goal.targetMinorUnits.toDouble()) * 100).toInt()
                val currentFormatted = CurrencyData.formatMoney(goal.currentMinorUnits, goal.currencyCode.ifBlank { defaultCurrency })
                val targetFormatted = CurrencyData.formatMoney(goal.targetMinorUnits, goal.currencyCode.ifBlank { defaultCurrency })

                val notifId = (30000 + goal.id).toInt()
                val (title, body) = if (prefs.privacyModeEnabled) {
                    "Savings goal update" to "${goal.name} is $percentage% complete."
                } else {
                    "Savings goal progress" to "$currentFormatted of $targetFormatted saved for ${goal.name} ($percentage%)."
                }

                MoneyFlowNotificationManager.showNotification(
                    context = context,
                    notificationId = notifId,
                    channelId = MoneyFlowNotificationManager.CHANNEL_SAVINGS,
                    title = title,
                    body = body,
                    destinationSubScreen = MoneyFlowNotificationManager.DESTINATION_SAVINGS_GOALS,
                    actionTitle = "View Goal"
                )
            }
        }
    }

    suspend fun processDailySummary(context: Context) {
        val app = context.applicationContext as? MoneyFlowApplication ?: return
        val prefsRepo = NotificationPreferencesRepository(context)
        val prefs = prefsRepo.getPreferences()
        if (!prefs.masterEnabled || !prefs.dailySummaryEnabled) return

        val userPrefs = app.userPreferencesRepository.userPreferencesFlow.first()
        val defaultCurrency = userPrefs.defaultCurrency

        val cal = Calendar.getInstance()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)

        // Prevent duplicate daily summary on the same calendar day
        if (prefs.lastDailySummaryDate == todayStr) return

        // Compute start of day and end of day millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000L - 1

        val allTransactions = app.database.transactionDao().getAllTransactions().first()
        val todayTransactions = allTransactions.filter { it.dateMillis in startOfDay..endOfDay }

        val todayExpenses = todayTransactions
            .filter { it.type == TransactionType.EXPENSE.name }
            .sumOf { it.amountMinorUnits }

        val (title, body) = if (todayExpenses > 0) {
            val formatted = CurrencyData.formatMoney(todayExpenses, defaultCurrency)
            if (prefs.privacyModeEnabled) {
                "Today's spending" to "You recorded spending today in MoneyFlow."
            } else {
                "Today's spending" to "You spent $formatted today."
            }
        } else {
            "Today's spending" to "No expenses recorded today."
        }

        MoneyFlowNotificationManager.showNotification(
            context = context,
            notificationId = 40001,
            channelId = MoneyFlowNotificationManager.CHANNEL_DAILY_SUMMARY,
            title = title,
            body = body,
            destinationTab = MoneyFlowNotificationManager.DESTINATION_TRANSACTIONS,
            actionTitle = "View Transactions"
        )

        prefsRepo.recordDailySummarySent(todayStr)
    }

    suspend fun processMonthlySummary(context: Context) {
        val app = context.applicationContext as? MoneyFlowApplication ?: return
        val prefsRepo = NotificationPreferencesRepository(context)
        val prefs = prefsRepo.getPreferences()
        if (!prefs.masterEnabled || !prefs.monthlySummaryEnabled) return

        val userPrefs = app.userPreferencesRepository.userPreferencesFlow.first()
        val defaultCurrency = userPrefs.defaultCurrency

        val cal = Calendar.getInstance()
        val monthStr = SimpleDateFormat("yyyy-MM", Locale.US).format(cal.time)

        // Prevent duplicate monthly summary in the same month
        if (prefs.lastMonthlySummaryMonth == monthStr) return

        // Compute previous calendar month
        cal.add(Calendar.MONTH, -1)
        val targetYear = cal.get(Calendar.YEAR)
        val targetMonth = cal.get(Calendar.MONTH)
        val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.time)

        val allTransactions = app.database.transactionDao().getAllTransactions().first()
        val txCal = Calendar.getInstance()

        var monthIncome = 0L
        var monthExpense = 0L

        for (tx in allTransactions) {
            txCal.timeInMillis = tx.dateMillis
            if (txCal.get(Calendar.YEAR) == targetYear && txCal.get(Calendar.MONTH) == targetMonth) {
                when (tx.type) {
                    TransactionType.INCOME.name -> monthIncome += tx.amountMinorUnits
                    TransactionType.EXPENSE.name -> monthExpense += tx.amountMinorUnits
                }
            }
        }

        val netSaved = (monthIncome - monthExpense).coerceAtLeast(0L)
        val incomeFormatted = CurrencyData.formatMoney(monthIncome, defaultCurrency)
        val expenseFormatted = CurrencyData.formatMoney(monthExpense, defaultCurrency)
        val savedFormatted = CurrencyData.formatMoney(netSaved, defaultCurrency)

        val (title, body) = if (prefs.privacyModeEnabled) {
            "$monthName money summary" to "Your monthly financial summary is ready to view in MoneyFlow."
        } else {
            "$monthName money summary" to "Income $incomeFormatted · Expenses $expenseFormatted · Saved $savedFormatted"
        }

        MoneyFlowNotificationManager.showNotification(
            context = context,
            notificationId = 40002,
            channelId = MoneyFlowNotificationManager.CHANNEL_MONTHLY_SUMMARY,
            title = title,
            body = body,
            destinationTab = MoneyFlowNotificationManager.DESTINATION_HOME,
            actionTitle = "View Summary"
        )

        prefsRepo.recordMonthlySummarySent(monthStr)
    }

    suspend fun sendTestNotification(context: Context) {
        val prefsRepo = NotificationPreferencesRepository(context)
        val prefs = prefsRepo.getPreferences()

        val (title, body) = if (prefs.privacyModeEnabled) {
            "MoneyFlow reminder" to "Your notification system is working perfectly."
        } else {
            "MoneyFlow test alert" to "All local notifications and channels are fully operational."
        }

        MoneyFlowNotificationManager.showNotification(
            context = context,
            notificationId = 50001,
            channelId = MoneyFlowNotificationManager.CHANNEL_BUDGET,
            title = title,
            body = body,
            actionTitle = "Open MoneyFlow"
        )
    }
}
