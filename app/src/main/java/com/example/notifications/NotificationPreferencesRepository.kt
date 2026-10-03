package com.example.notifications

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_preferences")

class NotificationPreferencesRepository(private val context: Context) {

    private object Keys {
        val MASTER_ENABLED = booleanPreferencesKey("notif_master_enabled")
        val RECURRING_ENABLED = booleanPreferencesKey("notif_recurring_enabled")
        val BUDGET_ALERTS_ENABLED = booleanPreferencesKey("notif_budget_alerts_enabled")
        val SAVINGS_GOALS_ENABLED = booleanPreferencesKey("notif_savings_goals_enabled")
        val DAILY_SUMMARY_ENABLED = booleanPreferencesKey("notif_daily_summary_enabled")
        val MONTHLY_SUMMARY_ENABLED = booleanPreferencesKey("notif_monthly_summary_enabled")

        val DAILY_SUMMARY_HOUR = intPreferencesKey("notif_daily_summary_hour")
        val DAILY_SUMMARY_MINUTE = intPreferencesKey("notif_daily_summary_minute")
        val MONTHLY_SUMMARY_DAY = intPreferencesKey("notif_monthly_summary_day")
        val MONTHLY_SUMMARY_HOUR = intPreferencesKey("notif_monthly_summary_hour")
        val MONTHLY_SUMMARY_MINUTE = intPreferencesKey("notif_monthly_summary_minute")

        val BUDGET_THRESHOLD_75 = booleanPreferencesKey("notif_budget_threshold_75")
        val BUDGET_THRESHOLD_90 = booleanPreferencesKey("notif_budget_threshold_90")
        val BUDGET_THRESHOLD_100 = booleanPreferencesKey("notif_budget_threshold_100")

        val PRIVACY_MODE_ENABLED = booleanPreferencesKey("notif_privacy_mode_enabled")

        val TRIGGERED_BUDGET_THRESHOLDS = stringSetPreferencesKey("notif_triggered_budget_thresholds")
        val TRIGGERED_GOAL_COMPLETED_IDS = stringSetPreferencesKey("notif_triggered_goal_completed_ids")
        val LAST_DAILY_SUMMARY_DATE = stringPreferencesKey("notif_last_daily_summary_date")
        val LAST_MONTHLY_SUMMARY_MONTH = stringPreferencesKey("notif_last_monthly_summary_month")
    }

    val preferencesFlow: Flow<NotificationPreferences> = context.notificationDataStore.data.map { prefs ->
        NotificationPreferences(
            masterEnabled = prefs[Keys.MASTER_ENABLED] ?: true,
            recurringEnabled = prefs[Keys.RECURRING_ENABLED] ?: true,
            budgetAlertsEnabled = prefs[Keys.BUDGET_ALERTS_ENABLED] ?: true,
            savingsGoalsEnabled = prefs[Keys.SAVINGS_GOALS_ENABLED] ?: true,
            dailySummaryEnabled = prefs[Keys.DAILY_SUMMARY_ENABLED] ?: false,
            monthlySummaryEnabled = prefs[Keys.MONTHLY_SUMMARY_ENABLED] ?: false,
            dailySummaryHour = prefs[Keys.DAILY_SUMMARY_HOUR] ?: 20,
            dailySummaryMinute = prefs[Keys.DAILY_SUMMARY_MINUTE] ?: 0,
            monthlySummaryDay = prefs[Keys.MONTHLY_SUMMARY_DAY] ?: 1,
            monthlySummaryHour = prefs[Keys.MONTHLY_SUMMARY_HOUR] ?: 9,
            monthlySummaryMinute = prefs[Keys.MONTHLY_SUMMARY_MINUTE] ?: 0,
            budgetThreshold75 = prefs[Keys.BUDGET_THRESHOLD_75] ?: true,
            budgetThreshold90 = prefs[Keys.BUDGET_THRESHOLD_90] ?: true,
            budgetThreshold100 = prefs[Keys.BUDGET_THRESHOLD_100] ?: true,
            privacyModeEnabled = prefs[Keys.PRIVACY_MODE_ENABLED] ?: false,
            lastTriggeredBudgetThresholds = prefs[Keys.TRIGGERED_BUDGET_THRESHOLDS] ?: emptySet(),
            lastTriggeredGoalCompletedIds = prefs[Keys.TRIGGERED_GOAL_COMPLETED_IDS] ?: emptySet(),
            lastDailySummaryDate = prefs[Keys.LAST_DAILY_SUMMARY_DATE] ?: "",
            lastMonthlySummaryMonth = prefs[Keys.LAST_MONTHLY_SUMMARY_MONTH] ?: ""
        )
    }

    suspend fun getPreferences(): NotificationPreferences = preferencesFlow.first()

    suspend fun setMasterEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { it[Keys.MASTER_ENABLED] = enabled }
    }

    suspend fun setRecurringEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { it[Keys.RECURRING_ENABLED] = enabled }
    }

    suspend fun setBudgetAlertsEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { it[Keys.BUDGET_ALERTS_ENABLED] = enabled }
    }

    suspend fun setSavingsGoalsEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { it[Keys.SAVINGS_GOALS_ENABLED] = enabled }
    }

    suspend fun setDailySummaryEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { it[Keys.DAILY_SUMMARY_ENABLED] = enabled }
    }

    suspend fun setMonthlySummaryEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { it[Keys.MONTHLY_SUMMARY_ENABLED] = enabled }
    }

    suspend fun setDailySummaryTime(hour: Int, minute: Int) {
        context.notificationDataStore.edit {
            it[Keys.DAILY_SUMMARY_HOUR] = hour
            it[Keys.DAILY_SUMMARY_MINUTE] = minute
        }
    }

    suspend fun setMonthlySummarySchedule(day: Int, hour: Int, minute: Int) {
        context.notificationDataStore.edit {
            it[Keys.MONTHLY_SUMMARY_DAY] = day
            it[Keys.MONTHLY_SUMMARY_HOUR] = hour
            it[Keys.MONTHLY_SUMMARY_MINUTE] = minute
        }
    }

    suspend fun setBudgetThreshold75(enabled: Boolean) {
        context.notificationDataStore.edit { it[Keys.BUDGET_THRESHOLD_75] = enabled }
    }

    suspend fun setBudgetThreshold90(enabled: Boolean) {
        context.notificationDataStore.edit { it[Keys.BUDGET_THRESHOLD_90] = enabled }
    }

    suspend fun setBudgetThreshold100(enabled: Boolean) {
        context.notificationDataStore.edit { it[Keys.BUDGET_THRESHOLD_100] = enabled }
    }

    suspend fun setPrivacyMode(enabled: Boolean) {
        context.notificationDataStore.edit { it[Keys.PRIVACY_MODE_ENABLED] = enabled }
    }

    suspend fun recordBudgetThresholdTriggered(key: String) {
        context.notificationDataStore.edit { prefs ->
            val current = prefs[Keys.TRIGGERED_BUDGET_THRESHOLDS] ?: emptySet()
            prefs[Keys.TRIGGERED_BUDGET_THRESHOLDS] = current + key
        }
    }

    suspend fun recordGoalCompletedTriggered(goalId: Long) {
        context.notificationDataStore.edit { prefs ->
            val current = prefs[Keys.TRIGGERED_GOAL_COMPLETED_IDS] ?: emptySet()
            prefs[Keys.TRIGGERED_GOAL_COMPLETED_IDS] = current + goalId.toString()
        }
    }

    suspend fun recordDailySummarySent(dateStr: String) {
        context.notificationDataStore.edit { it[Keys.LAST_DAILY_SUMMARY_DATE] = dateStr }
    }

    suspend fun recordMonthlySummarySent(monthStr: String) {
        context.notificationDataStore.edit { it[Keys.LAST_MONTHLY_SUMMARY_MONTH] = monthStr }
    }
}
