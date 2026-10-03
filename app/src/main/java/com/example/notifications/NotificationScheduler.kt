package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object NotificationScheduler {

    const val ACTION_TRIGGER_DAILY_SUMMARY = "com.example.moneyflow.ACTION_TRIGGER_DAILY_SUMMARY"
    const val ACTION_TRIGGER_MONTHLY_SUMMARY = "com.example.moneyflow.ACTION_TRIGGER_MONTHLY_SUMMARY"
    const val ACTION_TRIGGER_RECURRING_REMINDERS = "com.example.moneyflow.ACTION_TRIGGER_RECURRING_REMINDERS"

    private const val REQUEST_CODE_DAILY = 4001
    private const val REQUEST_CODE_MONTHLY = 4002
    private const val REQUEST_CODE_RECURRING = 4003

    fun scheduleAll(context: Context, prefs: NotificationPreferences) {
        if (!prefs.masterEnabled) {
            cancelAll(context)
            return
        }

        if (prefs.dailySummaryEnabled) {
            scheduleDailySummary(context, prefs.dailySummaryHour, prefs.dailySummaryMinute)
        } else {
            cancelDailySummary(context)
        }

        if (prefs.monthlySummaryEnabled) {
            scheduleMonthlySummary(context, prefs.monthlySummaryDay, prefs.monthlySummaryHour, prefs.monthlySummaryMinute)
        } else {
            cancelMonthlySummary(context)
        }

        if (prefs.recurringEnabled) {
            scheduleRecurringReminders(context)
        } else {
            cancelRecurringReminders(context)
        }
    }

    fun scheduleDailySummary(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If already passed today, schedule for tomorrow
            if (timeInMillis <= now) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, MoneyFlowNotificationReceiver::class.java).apply {
            action = ACTION_TRIGGER_DAILY_SUMMARY
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAILY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarmSafely(alarmManager, calendar.timeInMillis, pendingIntent)
    }

    fun scheduleMonthlySummary(context: Context, dayOfMonth: Int, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, dayOfMonth.coerceIn(1, 28)) // Safe boundary for all months
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If already passed this month, schedule for 1st of next month
            if (timeInMillis <= now) {
                add(Calendar.MONTH, 1)
                set(Calendar.DAY_OF_MONTH, dayOfMonth.coerceIn(1, 28))
            }
        }

        val intent = Intent(context, MoneyFlowNotificationReceiver::class.java).apply {
            action = ACTION_TRIGGER_MONTHLY_SUMMARY
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_MONTHLY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarmSafely(alarmManager, calendar.timeInMillis, pendingIntent)
    }

    fun scheduleRecurringReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Daily recurring scan around 9:00 AM
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, MoneyFlowNotificationReceiver::class.java).apply {
            action = ACTION_TRIGGER_RECURRING_REMINDERS
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_RECURRING,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarmSafely(alarmManager, calendar.timeInMillis, pendingIntent)
    }

    private fun setAlarmSafely(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        try {
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            if (canScheduleExact) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                // Inexact fallback without requiring exact alarm permissions
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            // Graceful fallback to inexact standard alarm if permission check fails
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } catch (_: Exception) {
                // Prevent any unhandled crash
            }
        }
    }

    fun cancelDailySummary(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, MoneyFlowNotificationReceiver::class.java).apply {
            action = ACTION_TRIGGER_DAILY_SUMMARY
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAILY,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun cancelMonthlySummary(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, MoneyFlowNotificationReceiver::class.java).apply {
            action = ACTION_TRIGGER_MONTHLY_SUMMARY
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_MONTHLY,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun cancelRecurringReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, MoneyFlowNotificationReceiver::class.java).apply {
            action = ACTION_TRIGGER_RECURRING_REMINDERS
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_RECURRING,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun cancelAll(context: Context) {
        cancelDailySummary(context)
        cancelMonthlySummary(context)
        cancelRecurringReminders(context)
    }
}
