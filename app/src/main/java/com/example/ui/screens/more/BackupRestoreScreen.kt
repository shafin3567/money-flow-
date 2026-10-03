package com.example.ui.screens.more

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ui.components.MoneyFlowTextField
import com.example.ui.theme.MoneyFlowTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.repository.BackupMetadata
import com.example.ui.components.MoneyFlowCard
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.NegativeCoral
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PowderBlue
import com.example.ui.theme.PowderBlueAccent
import kotlinx.coroutines.launch

@Composable
fun BackupRestoreScreen(
    onExportBackupJson: suspend () -> String,
    onRestoreBackupJson: suspend (String, Boolean) -> Result<BackupMetadata>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var exportedJsonText by remember { mutableStateOf<String?>(null) }
    var restoreJsonInput by remember { mutableStateOf("") }
    var restoreResult by remember { mutableStateOf<Result<BackupMetadata>?>(null) }
    var showRestoreConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            text = "Backup & Restore",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        )
                        Text(
                            text = "100% offline local data portability",
                            style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                        )
                    }
                }
            }
        }

        // Export Section
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(PowderBlue.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = null,
                                    tint = PowderBlueAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Export Local Backup",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                )
                                Text(
                                    text = "Generates complete JSON backup of accounts, transactions, and budgets",
                                    style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        PrimaryButton(
                            text = "Export Backup to JSON",
                            onClick = {
                                scope.launch {
                                    val json = onExportBackupJson()
                                    exportedJsonText = json
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("MoneyFlow Backup", json))
                                    Toast.makeText(context, "Backup copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        if (exportedJsonText != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "✓ Backup generated & copied to clipboard! You can safely paste and store it in a secure location.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PositiveGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }

        // Restore Section
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFECE9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    tint = NegativeCoral,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Restore from Backup",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                )
                                Text(
                                    text = "Paste a MoneyFlow JSON backup to restore your data",
                                    style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        MoneyFlowTextField(
                            value = restoreJsonInput,
                            onValueChange = {
                                restoreJsonInput = it
                                restoreResult = null
                            },
                            placeholder = { Text("Paste valid MoneyFlow JSON here...") },
                            singleLine = false,
                            maxLines = 6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        PrimaryButton(
                            text = "Restore Data",
                            enabled = restoreJsonInput.isNotBlank(),
                            containerColor = NegativeCoral,
                            onClick = { showRestoreConfirm = true }
                        )

                        if (restoreResult != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            if (restoreResult!!.isSuccess) {
                                val meta = restoreResult!!.getOrNull()
                                Text(
                                    text = "✓ Successfully restored ${meta?.transactionsCount ?: 0} transactions and ${meta?.accountsCount ?: 0} accounts!",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = PositiveGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            } else {
                                Text(
                                    text = "Failed to restore: ${restoreResult!!.exceptionOrNull()?.localizedMessage ?: "Invalid format"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = NegativeCoral,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRestoreConfirm) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirm = false },
            title = {
                Text(
                    text = "Confirm Data Restore",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text("Restoring from this backup will merge and update your local database. Do you wish to proceed?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestoreConfirm = false
                        scope.launch {
                            val res = onRestoreBackupJson(restoreJsonInput.trim(), false)
                            restoreResult = res
                        }
                    }
                ) {
                    Text("Confirm Restore", color = NegativeCoral, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirm = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }
}
