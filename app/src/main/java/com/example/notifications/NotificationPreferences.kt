package com.example.notifications

/**
 * Persisted user preferences for the local notification system.
 */
data class NotificationPreferences(
    val masterEnabled: Boolean = true,
    val recurringEnabled: Boolean = true,
    val budgetAlertsEnabled: Boolean = true,
    val savingsGoalsEnabled: Boolean = true,
    val dailySummaryEnabled: Boolean = false,
    val monthlySummaryEnabled: Boolean = false,
    val dailySummaryHour: Int = 20, // 8:00 PM
    val dailySummaryMinute: Int = 0,
    val monthlySummaryDay: Int = 1, // 1st of month
    val monthlySummaryHour: Int = 9, // 9:00 AM
    val monthlySummaryMinute: Int = 0,
    val budgetThreshold75: Boolean = true,
    val budgetThreshold90: Boolean = true,
    val budgetThreshold100: Boolean = true,
    val privacyModeEnabled: Boolean = false, // When true, masks financial amounts in notification previews
    val lastTriggeredBudgetThresholds: Set<String> = emptySet(), // Format: "budget_{id}_{monthYear}_{threshold}"
    val lastTriggeredGoalCompletedIds: Set<String> = emptySet(), // Goal IDs that reached 100% and alerted
    val lastDailySummaryDate: String = "", // e.g. "2026-10-03"
    val lastMonthlySummaryMonth: String = "" // e.g. "2026-10"
)
