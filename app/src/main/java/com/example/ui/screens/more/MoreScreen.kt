package com.example.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.UserPreferences
import com.example.ui.components.Clay3dSphere
import com.example.ui.components.CurrencySelectorDialog
import com.example.ui.components.MoneyFlowCard
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.MoneyFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    userPreferences: UserPreferences,
    onNavigateToAccounts: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToRecurring: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onUpdateCurrency: (String) -> Unit,
    onUpdateSecurity: (enabled: Boolean, pin: String, useBiometrics: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "More Options",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = colors.textPrimary,
                        letterSpacing = (-0.5).sp
                    )
                )
                Text(
                    text = "Preferences, data & security",
                    style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
                )
            }
        }

        // Finance Hub Group
        item {
            SectionTitle(title = "FINANCE HUB")
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                    Column {
                        MoreItemRow(
                            icon = Icons.Default.AccountBalanceWallet,
                            sphereColor = Color(0xFF7FA7C4),
                            title = "Accounts & Wallets",
                            subtitle = "Cash, bank accounts, and credit cards",
                            onClick = onNavigateToAccounts
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        MoreItemRow(
                            icon = Icons.Default.Savings,
                            sphereColor = Color(0xFFE89D86),
                            title = "Savings Goals",
                            subtitle = "Target funds, progress and deposits",
                            onClick = onNavigateToGoals
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        MoreItemRow(
                            icon = Icons.Default.EventRepeat,
                            sphereColor = Color(0xFF5BA678),
                            title = "Recurring Transactions",
                            subtitle = "Manage regular subscriptions and rent",
                            onClick = onNavigateToRecurring
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        MoreItemRow(
                            icon = Icons.Default.Analytics,
                            sphereColor = Color(0xFFE5AA42),
                            title = "Spending Analytics",
                            subtitle = "Visual trends, cash flows, and category breakdowns",
                            onClick = onNavigateToAnalytics
                        )
                    }
                }
            }
        }

        // Preferences & Security
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle(title = "PREFERENCES & PRIVACY")
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                    Column {
                        MoreItemRow(
                            icon = Icons.Default.AttachMoney,
                            sphereColor = Color(0xFF7FA7C4),
                            title = "Primary Currency",
                            subtitle = "Current: ${userPreferences.defaultCurrency}",
                            onClick = { showCurrencyDialog = true }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        MoreItemRow(
                            icon = Icons.Default.Shield,
                            sphereColor = if (userPreferences.isAppLockEnabled) Color(0xFF5BA678) else Color(0xFF9A8B8C),
                            title = "Security & App Lock",
                            subtitle = if (userPreferences.isAppLockEnabled) "App Lock is Active (PIN / Biometrics)" else "Disabled (Tap to protect)",
                            onClick = { showSecurityDialog = true }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        MoreItemRow(
                            icon = Icons.Default.CloudDownload,
                            sphereColor = Color(0xFF7FA7C4),
                            title = "Backup & Restore",
                            subtitle = "Export or import local JSON data",
                            onClick = onNavigateToBackup
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        MoreItemRow(
                            icon = Icons.Default.Info,
                            sphereColor = Color(0xFFE89D86),
                            title = "About MoneyFlow",
                            subtitle = "Version 1.0 • 100% Offline & Private",
                            onClick = { showAboutDialog = true }
                        )
                    }
                }
            }
        }
    }

    if (showCurrencyDialog) {
        CurrencySelectorDialog(
            selectedCurrencyCode = userPreferences.defaultCurrency,
            onCurrencySelected = { item ->
                onUpdateCurrency(item.code)
            },
            onDismissRequest = { showCurrencyDialog = false }
        )
    }

    if (showSecurityDialog) {
        SecurityConfigDialog(
            currentEnabled = userPreferences.isAppLockEnabled,
            currentUseBiometrics = userPreferences.useBiometrics,
            onDismissRequest = { showSecurityDialog = false },
            onSave = { enabled, pin, bio ->
                onUpdateSecurity(enabled, pin, bio)
                showSecurityDialog = false
            }
        )
    }

    if (showAboutDialog) {
        BasicAlertDialog(onDismissRequest = { showAboutDialog = false }) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = if (colors.isDark) colors.surfaceElevated else Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "MoneyFlow",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = colors.textPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Understand your money. Build better habits.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "• 100% Offline & Device-Encrypted\n• Multi-Account & Category Budgets\n• Precise Safe Integer Financial Calculations\n• Full JSON Backup & Restore",
                        style = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary, lineHeight = 22.sp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = { showAboutDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold, color = colors.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.3.sp,
            color = MoneyFlowTheme.colors.textTertiary
        ),
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun MoreItemRow(
    icon: ImageVector,
    sphereColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = MoneyFlowTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            Clay3dSphere(
                baseColor = sphereColor,
                size = 44.dp
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = 15.sp
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.textTertiary,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = colors.textTertiary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SecurityConfigDialog(
    currentEnabled: Boolean,
    currentUseBiometrics: Boolean,
    onDismissRequest: () -> Unit,
    onSave: (enabled: Boolean, pin: String, useBiometrics: Boolean) -> Unit
) {
    val colors = MoneyFlowTheme.colors
    var isEnabled by remember { mutableStateOf(currentEnabled) }
    var pin by remember { mutableStateOf("") }
    var useBio by remember { mutableStateOf(currentUseBiometrics) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Surface(
        shape = RoundedCornerShape(32.dp),
        color = if (colors.isDark) colors.surfaceElevated else Color.White,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "App Lock Security",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enable App Lock",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { isEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.primary
                    )
                )
            }

            if (isEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                            pin = it
                            errorText = null
                        }
                    },
                    label = { Text("Set 4-6 digit PIN") },
                    isError = errorText != null,
                    supportingText = errorText?.let { { Text(it, color = colors.error) } },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.border
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Allow Biometric Unlock",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = useBio,
                        onCheckedChange = { useBio = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = colors.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel", color = colors.textSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                PrimaryButton(
                    text = "Save",
                    onClick = {
                        if (isEnabled && pin.length < 4) {
                            errorText = "Please enter at least a 4-digit PIN"
                        } else {
                            onSave(isEnabled, pin, useBio)
                        }
                    }
                )
            }
        }
    }
}
