package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoneyFlowNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefsRepo = NotificationPreferencesRepository(context)
                val prefs = prefsRepo.getPreferences()

                when (action) {
                    Intent.ACTION_BOOT_COMPLETED,
                    Intent.ACTION_LOCKED_BOOT_COMPLETED,
                    Intent.ACTION_TIME_CHANGED,
                    Intent.ACTION_TIMEZONE_CHANGED -> {
                        // Re-initialize notification channels
                        MoneyFlowNotificationManager.createNotificationChannels(context)

                        // Reschedule all alarms based on updated time/timezone or system boot
                        NotificationScheduler.scheduleAll(context, prefs)

                        // Check if any recurring bills or budgets need immediate attention
                        if (prefs.masterEnabled) {
                            if (prefs.recurringEnabled) {
                                NotificationProcessor.processRecurringReminders(context)
                            }
                            if (prefs.budgetAlertsEnabled) {
                                NotificationProcessor.processBudgetThresholdAlerts(context)
                            }
                        }
                    }

                    NotificationScheduler.ACTION_TRIGGER_DAILY_SUMMARY -> {
                        if (prefs.masterEnabled && prefs.dailySummaryEnabled) {
                            NotificationProcessor.processDailySummary(context)
                            // Reschedule for next calendar day
                            NotificationScheduler.scheduleDailySummary(
                                context,
                                prefs.dailySummaryHour,
                                prefs.dailySummaryMinute
                            )
                        }
                    }

                    NotificationScheduler.ACTION_TRIGGER_MONTHLY_SUMMARY -> {
                        if (prefs.masterEnabled && prefs.monthlySummaryEnabled) {
                            NotificationProcessor.processMonthlySummary(context)
                            // Reschedule for next month
                            NotificationScheduler.scheduleMonthlySummary(
                                context,
                                prefs.monthlySummaryDay,
                                prefs.monthlySummaryHour,
                                prefs.monthlySummaryMinute
                            )
                        }
                    }

                    NotificationScheduler.ACTION_TRIGGER_RECURRING_REMINDERS -> {
                        if (prefs.masterEnabled && prefs.recurringEnabled) {
                            NotificationProcessor.processRecurringReminders(context)
                            // Reschedule next daily recurring check
                            NotificationScheduler.scheduleRecurringReminders(context)
                        }
                    }
                }
            } catch (_: Exception) {
                // Prevent unhandled crashes in broadcast receiver
            } finally {
                pendingResult.finish()
            }
        }
    }
}
