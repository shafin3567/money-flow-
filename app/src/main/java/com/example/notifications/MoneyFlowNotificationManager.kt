package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object MoneyFlowNotificationManager {

    const val CHANNEL_BILLS = "moneyflow_bills"
    const val CHANNEL_BUDGET = "moneyflow_budget"
    const val CHANNEL_SAVINGS = "moneyflow_savings"
    const val CHANNEL_DAILY_SUMMARY = "moneyflow_daily_summary"
    const val CHANNEL_MONTHLY_SUMMARY = "moneyflow_monthly_summary"

    const val EXTRA_DESTINATION_SUB_SCREEN = "extra_destination_sub_screen"
    const val EXTRA_DESTINATION_TAB = "extra_destination_tab"

    const val DESTINATION_BUDGETS = "BUDGETS"
    const val DESTINATION_RECURRING = "RECURRING"
    const val DESTINATION_SAVINGS_GOALS = "SAVINGS_GOALS"
    const val DESTINATION_TRANSACTIONS = "TRANSACTIONS"
    const val DESTINATION_HOME = "HOME"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_BILLS,
                    "Bills & Recurring Payments",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts for upcoming recurring expenses, bills, and subscriptions"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_BUDGET,
                    "Budget Alerts & Limits",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Alerts when spending reaches or exceeds your configured budget thresholds"
                },
                NotificationChannel(
                    CHANNEL_SAVINGS,
                    "Savings Goals",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Progress updates and milestone celebrations for your savings goals"
                },
                NotificationChannel(
                    CHANNEL_DAILY_SUMMARY,
                    "Daily Spending Summary",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Evening recap of today's total spending and transactions"
                    enableVibration(false)
                },
                NotificationChannel(
                    CHANNEL_MONTHLY_SUMMARY,
                    "Monthly Financial Summary",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Monthly money recap of total income, expenses, and savings"
                }
            )

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        body: String,
        destinationSubScreen: String? = null,
        destinationTab: String? = null,
        actionTitle: String? = null
    ) {
        if (!hasNotificationPermission(context)) {
            return
        }

        // Tap Intent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            destinationSubScreen?.let { putExtra(EXTRA_DESTINATION_SUB_SCREEN, it) }
            destinationTab?.let { putExtra(EXTRA_DESTINATION_TAB, it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(
                when (channelId) {
                    CHANNEL_BILLS -> NotificationCompat.PRIORITY_HIGH
                    CHANNEL_DAILY_SUMMARY -> NotificationCompat.PRIORITY_LOW
                    else -> NotificationCompat.PRIORITY_DEFAULT
                }
            )

        // Action button if specified
        if (!actionTitle.isNullOrBlank()) {
            builder.addAction(
                0,
                actionTitle,
                pendingIntent
            )
        }

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled gracefully if permission revoked between check and notify
        }
    }
}
