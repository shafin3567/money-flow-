package com.example.ui.screens.more

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notifications.MoneyFlowNotificationManager
import com.example.notifications.NotificationPreferences
import com.example.ui.components.MoneyFlowCard
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.MoneyFlowTheme
import com.example.ui.theme.NegativeCoral
import com.example.ui.theme.PositiveGreen
import java.util.Locale

@Composable
fun NotificationsScreen(
    preferences: NotificationPreferences,
    onBackClick: () -> Unit,
    onToggleMaster: (Boolean) -> Unit,
    onToggleRecurring: (Boolean) -> Unit,
    onToggleBudgets: (Boolean) -> Unit,
    onToggleGoals: (Boolean) -> Unit,
    onToggleDaily: (Boolean) -> Unit,
    onToggleMonthly: (Boolean) -> Unit,
    onUpdateDailyTime: (hour: Int, minute: Int) -> Unit,
    onUpdateMonthlySchedule: (day: Int, hour: Int, minute: Int) -> Unit,
    onToggleThreshold75: (Boolean) -> Unit,
    onToggleThreshold90: (Boolean) -> Unit,
    onToggleThreshold100: (Boolean) -> Unit,
    onTogglePrivacy: (Boolean) -> Unit,
    onSendTestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = MoneyFlowTheme.colors

    var hasSystemPermission by remember {
        mutableStateOf(MoneyFlowNotificationManager.hasNotificationPermission(context))
    }
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showDailyTimeDialog by remember { mutableStateOf(false) }
    var showMonthlyTimeDialog by remember { mutableStateOf(false) }
    var testNotifSentMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasSystemPermission = isGranted
        if (isGranted) {
            onToggleMaster(true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 40.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        )
                        Text(
                            text = "100% local, offline financial alerts",
                            style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                        )
                    }
                }
            }

            // Android System Permission Warning Banner (if denied)
            if (!hasSystemPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        MoneyFlowCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = colors.surfaceElevated
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsOff,
                                        contentDescription = null,
                                        tint = NegativeCoral,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Notifications are disabled in Android",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Android notification permissions are required to receive local bill reminders and budget alerts.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                PrimaryButton(
                                    text = "Open App Notification Settings",
                                    containerColor = colors.primary,
                                    onClick = {
                                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                        }
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Master Notifications Toggle
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (preferences.masterEnabled) colors.primaryContainer else colors.surfaceSubtle),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (preferences.masterEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                        contentDescription = null,
                                        tint = if (preferences.masterEnabled) colors.primary else colors.textTertiary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Master Notifications",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                    )
                                    Text(
                                        text = if (preferences.masterEnabled) "All local alerts active" else "All notifications silenced",
                                        style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                                    )
                                }
                            }

                            Switch(
                                checked = preferences.masterEnabled,
                                onCheckedChange = { newState ->
                                    if (newState && !hasSystemPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        showRationaleDialog = true
                                    } else {
                                        onToggleMaster(newState)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.primary
                                )
                            )
                        }
                    }
                }
            }

            // Notification Types Section
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeader(title = "NOTIFICATION CATEGORIES")
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        Column {
                            NotificationToggleRow(
                                title = "Recurring Bills & Payments",
                                subtitle = "Alert when scheduled subscriptions or bills are due",
                                icon = Icons.Default.Notifications,
                                isChecked = preferences.recurringEnabled && preferences.masterEnabled,
                                enabled = preferences.masterEnabled,
                                onCheckedChange = onToggleRecurring
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            NotificationToggleRow(
                                title = "Budget Alerts & Thresholds",
                                subtitle = "Warn when spending approaches 75%, 90% or 100%",
                                icon = Icons.Default.PieChart,
                                isChecked = preferences.budgetAlertsEnabled && preferences.masterEnabled,
                                enabled = preferences.masterEnabled,
                                onCheckedChange = onToggleBudgets
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            NotificationToggleRow(
                                title = "Savings Goals Updates",
                                subtitle = "Milestone alerts and 100% completion celebrations",
                                icon = Icons.Default.Savings,
                                isChecked = preferences.savingsGoalsEnabled && preferences.masterEnabled,
                                enabled = preferences.masterEnabled,
                                onCheckedChange = onToggleGoals
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            NotificationToggleRow(
                                title = "Daily Spending Summary",
                                subtitle = "Evening recap of today's total spending",
                                icon = Icons.Default.Today,
                                isChecked = preferences.dailySummaryEnabled && preferences.masterEnabled,
                                enabled = preferences.masterEnabled,
                                onCheckedChange = onToggleDaily
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            NotificationToggleRow(
                                title = "Monthly Financial Summary",
                                subtitle = "End-of-month recap of income, expenses, and savings",
                                icon = Icons.Default.CalendarMonth,
                                isChecked = preferences.monthlySummaryEnabled && preferences.masterEnabled,
                                enabled = preferences.masterEnabled,
                                onCheckedChange = onToggleMonthly
                            )
                        }
                    }
                }
            }

            // Schedule & Timing Section
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeader(title = "NOTIFICATION TIMING")
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        Column {
                            // Daily Summary Time Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = preferences.masterEnabled && preferences.dailySummaryEnabled) {
                                        showDailyTimeDialog = true
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = if (preferences.masterEnabled && preferences.dailySummaryEnabled) colors.primary else colors.textDisabled,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Daily Summary Time",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (preferences.masterEnabled && preferences.dailySummaryEnabled) colors.textPrimary else colors.textDisabled
                                            )
                                        )
                                        Text(
                                            text = formatTime(preferences.dailySummaryHour, preferences.dailySummaryMinute),
                                            style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                                        )
                                    }
                                }
                                Text(
                                    text = "Change",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (preferences.masterEnabled && preferences.dailySummaryEnabled) colors.primary else colors.textDisabled,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Monthly Summary Schedule Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = preferences.masterEnabled && preferences.monthlySummaryEnabled) {
                                        showMonthlyTimeDialog = true
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = if (preferences.masterEnabled && preferences.monthlySummaryEnabled) colors.primary else colors.textDisabled,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Monthly Summary Time",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (preferences.masterEnabled && preferences.monthlySummaryEnabled) colors.textPrimary else colors.textDisabled
                                            )
                                        )
                                        Text(
                                            text = "${getOrdinal(preferences.monthlySummaryDay)} day of month · ${formatTime(preferences.monthlySummaryHour, preferences.monthlySummaryMinute)}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                                        )
                                    }
                                }
                                Text(
                                    text = "Change",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (preferences.masterEnabled && preferences.monthlySummaryEnabled) colors.primary else colors.textDisabled,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Budget Alert Thresholds Section
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeader(title = "BUDGET ALERT THRESHOLDS")
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        Column {
                            ThresholdToggleRow(
                                label = "75% of limit reached",
                                description = "Early warning before exceeding allowance",
                                isChecked = preferences.budgetThreshold75 && preferences.budgetAlertsEnabled && preferences.masterEnabled,
                                enabled = preferences.masterEnabled && preferences.budgetAlertsEnabled,
                                onCheckedChange = onToggleThreshold75
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            ThresholdToggleRow(
                                label = "90% of limit reached",
                                description = "Urgent reminder to slow down spending",
                                isChecked = preferences.budgetThreshold90 && preferences.budgetAlertsEnabled && preferences.masterEnabled,
                                enabled = preferences.masterEnabled && preferences.budgetAlertsEnabled,
                                onCheckedChange = onToggleThreshold90
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            ThresholdToggleRow(
                                label = "100% Limit Exceeded",
                                description = "Alert immediately when budget has been overrun",
                                isChecked = preferences.budgetThreshold100 && preferences.budgetAlertsEnabled && preferences.masterEnabled,
                                enabled = preferences.masterEnabled && preferences.budgetAlertsEnabled,
                                onCheckedChange = onToggleThreshold100
                            )
                        }
                    }
                }
            }

            // Privacy & System Audit Section
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeader(title = "PRIVACY & SECURITY")
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        Column {
                            NotificationToggleRow(
                                title = "Notification Privacy Mode",
                                subtitle = "Hide exact balances and amounts from preview banners",
                                icon = Icons.Default.Security,
                                isChecked = preferences.privacyModeEnabled,
                                enabled = true,
                                onCheckedChange = onTogglePrivacy
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Test Notification Button
                            PrimaryButton(
                                text = "Send Test Local Notification",
                                onClick = {
                                    onSendTestNotification()
                                    testNotifSentMessage = "Test notification sent! Check your notification shade."
                                }
                            )

                            if (testNotifSentMessage != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = testNotifSentMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = PositiveGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Footer note
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = colors.textTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100% offline. Notifications are scheduled via Android AlarmManager and read directly from local Room data.",
                        style = MaterialTheme.typography.bodySmall.copy(color = colors.textTertiary)
                    )
                }
            }
        }
    }

    // Permission Rationale Dialog
    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = {
                Text(
                    text = "Enable MoneyFlow Notifications",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "MoneyFlow uses notifications exclusively on your device to alert you when recurring bills are due, warn when budgets are approaching their limits, and provide daily and monthly financial summaries. Your data stays 100% private and offline."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationaleDialog = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onToggleMaster(true)
                        }
                    }
                ) {
                    Text("Allow Notifications", color = colors.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) {
                    Text("Not Now", color = colors.textSecondary)
                }
            }
        )
    }

    // Daily Time Dialog
    if (showDailyTimeDialog) {
        TimeSelectionDialog(
            currentHour = preferences.dailySummaryHour,
            currentMinute = preferences.dailySummaryMinute,
            onDismissRequest = { showDailyTimeDialog = false },
            onTimeSelected = { h, m ->
                onUpdateDailyTime(h, m)
                showDailyTimeDialog = false
            }
        )
    }

    // Monthly Schedule Dialog
    if (showMonthlyTimeDialog) {
        MonthlyScheduleDialog(
            currentDay = preferences.monthlySummaryDay,
            currentHour = preferences.monthlySummaryHour,
            currentMinute = preferences.monthlySummaryMinute,
            onDismissRequest = { showMonthlyTimeDialog = false },
            onScheduleSelected = { d, h, m ->
                onUpdateMonthlySchedule(d, h, m)
                showMonthlyTimeDialog = false
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MoneyFlowTheme.colors.textTertiary,
            letterSpacing = 1.3.sp
        ),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
    )
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = MoneyFlowTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (enabled && isChecked) colors.primaryContainer else colors.surfaceSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled && isChecked) colors.primary else colors.textTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (enabled) colors.textPrimary else colors.textDisabled
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (enabled) colors.textSecondary else colors.textDisabled
                    )
                )
            }
        }
        Switch(
            checked = isChecked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.primary
            )
        )
    }
}

@Composable
private fun ThresholdToggleRow(
    label: String,
    description: String,
    isChecked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = MoneyFlowTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled) colors.textPrimary else colors.textDisabled
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (enabled) colors.textSecondary else colors.textDisabled
                )
            )
        }
        Switch(
            checked = isChecked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.primary
            )
        )
    }
}

@Composable
private fun TimeSelectionDialog(
    currentHour: Int,
    currentMinute: Int,
    onDismissRequest: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    val colors = MoneyFlowTheme.colors
    var selectedHour by remember { mutableStateOf(currentHour) }
    var selectedMinute by remember { mutableStateOf(currentMinute) }

    val presetTimes = listOf(
        Pair(18, 0) to "6:00 PM",
        Pair(19, 0) to "7:00 PM",
        Pair(20, 0) to "8:00 PM (Default)",
        Pair(21, 0) to "9:00 PM",
        Pair(22, 0) to "10:00 PM"
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Select Daily Summary Time",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Choose when you would like to receive your evening spending recap:",
                    style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
                )
                Spacer(modifier = Modifier.height(14.dp))
                presetTimes.forEach { (time, label) ->
                    val isSelected = selectedHour == time.first && selectedMinute == time.second
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) colors.primaryContainer else Color.Transparent)
                            .clickable {
                                selectedHour = time.first
                                selectedMinute = time.second
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) colors.primary else colors.textPrimary
                            )
                        )
                        if (isSelected) {
                            Text("✓", color = colors.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onTimeSelected(selectedHour, selectedMinute) }) {
                Text("Save", color = colors.primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", color = colors.textSecondary)
            }
        }
    )
}

@Composable
private fun MonthlyScheduleDialog(
    currentDay: Int,
    currentHour: Int,
    currentMinute: Int,
    onDismissRequest: () -> Unit,
    onScheduleSelected: (day: Int, hour: Int, minute: Int) -> Unit
) {
    val colors = MoneyFlowTheme.colors
    var selectedDay by remember { mutableStateOf(currentDay) }
    var selectedHour by remember { mutableStateOf(currentHour) }
    var selectedMinute by remember { mutableStateOf(currentMinute) }

    val presetDays = listOf(
        Triple(1, 9, 0) to "1st of Month · 9:00 AM (Default)",
        Triple(1, 20, 0) to "1st of Month · 8:00 PM",
        Triple(28, 9, 0) to "28th of Month · 9:00 AM",
        Triple(28, 20, 0) to "28th of Month · 8:00 PM"
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Select Monthly Summary Time",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Choose when your monthly financial review should be delivered:",
                    style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
                )
                Spacer(modifier = Modifier.height(14.dp))
                presetDays.forEach { (time, label) ->
                    val isSelected = selectedDay == time.first && selectedHour == time.second && selectedMinute == time.third
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) colors.primaryContainer else Color.Transparent)
                            .clickable {
                                selectedDay = time.first
                                selectedHour = time.second
                                selectedMinute = time.third
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) colors.primary else colors.textPrimary
                            )
                        )
                        if (isSelected) {
                            Text("✓", color = colors.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onScheduleSelected(selectedDay, selectedHour, selectedMinute) }) {
                Text("Save", color = colors.primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", color = colors.textSecondary)
            }
        }
    )
}

private fun formatTime(hour: Int, minute: Int): String {
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)
}

private fun getOrdinal(n: Int): String {
    return when {
        n in 11..13 -> "${n}th"
        n % 10 == 1 -> "${n}st"
        n % 10 == 2 -> "${n}nd"
        n % 10 == 3 -> "${n}rd"
        else -> "${n}th"
    }
}
