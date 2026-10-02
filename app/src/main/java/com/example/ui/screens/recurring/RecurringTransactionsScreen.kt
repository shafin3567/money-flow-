package com.example.ui.screens.recurring

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Update
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.RecurringTransactionEntity
import com.example.data.model.CurrencyData
import com.example.data.model.TransactionType
import com.example.ui.components.Clay3dSphere
import com.example.ui.components.EmptyStateView
import com.example.ui.components.MoneyFlowCard
import com.example.ui.theme.MoneyFlowTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecurringTransactionsScreen(
    recurringList: List<RecurringTransactionEntity>,
    accounts: List<AccountEntity>,
    onBackClick: () -> Unit,
    onAddRecurringClick: () -> Unit,
    onToggleActive: (RecurringTransactionEntity) -> Unit,
    onDeleteRecurring: (RecurringTransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    var itemToDelete by remember { mutableStateOf<RecurringTransactionEntity?>(null) }
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (colors.isDark) colors.surfaceElevated else Color.White)
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
                                text = "Recurring",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = colors.textPrimary,
                                    letterSpacing = (-0.5).sp,
                                    fontSize = 24.sp
                                ),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Subscriptions & regular income",
                                style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // 3D Sphere Add Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Clay3dSphere(
                            baseColor = Color(0xFF5BA678),
                            size = 46.dp
                        )
                        IconButton(onClick = onAddRecurringClick) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Recurring",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        if (recurringList.isEmpty()) {
            item {
                Box(modifier = Modifier.padding(24.dp)) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        EmptyStateView(
                            icon = Icons.Default.EventRepeat,
                            title = "No recurring schedules",
                            description = "Keep track of Netflix, rent, gym, or recurring salary deposits automatically.",
                            actionButtonText = "Create Schedule",
                            onActionClick = onAddRecurringClick
                        )
                    }
                }
            }
        } else {
            items(recurringList, key = { it.id }) { item ->
                val isExpense = item.type == TransactionType.EXPENSE.name
                val isIncome = item.type == TransactionType.INCOME.name
                val amountColor = if (isIncome) Color(0xFF43A047) else Color(0xFFE26B58)
                val amountPrefix = if (isExpense) "-" else if (isIncome) "+" else ""
                val amountFormatted = CurrencyData.formatMoney(item.amountMinorUnits, item.currencyCode)
                val nextDateStr = dateFormat.format(Date(item.nextDueDateMillis))
                val account = accounts.find { it.id == item.accountId }

                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    MoneyFlowCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        onClick = { itemToDelete = item }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(46.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Clay3dSphere(
                                    baseColor = if (isIncome) Color(0xFF5BA678) else Color(0xFFE26B58),
                                    size = 46.dp
                                )
                                Icon(
                                    imageVector = Icons.Default.Update,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
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
                                    text = "${item.frequency.lowercase().replaceFirstChar { it.uppercase() }} • Next: $nextDateStr",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colors.textTertiary,
                                        fontSize = 12.sp
                                    ),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (account != null) {
                                    Text(
                                        text = "Account: ${account.name}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = colors.textSecondary,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$amountPrefix$amountFormatted",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = amountColor,
                                        fontSize = 14.5.sp,
                                        letterSpacing = (-0.2).sp
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Switch(
                                    checked = item.isActive,
                                    onCheckedChange = { onToggleActive(item) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF7FA7C4)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (itemToDelete != null) {
        val r = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text("Delete Recurring Schedule?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Text("Do you want to delete '${r.title}'? Past transactions generated from this schedule will remain safe.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteRecurring(r)
                        itemToDelete = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFE26B58), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
