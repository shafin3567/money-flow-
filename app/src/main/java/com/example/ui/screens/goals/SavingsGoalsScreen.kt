package com.example.ui.screens.goals

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.model.CurrencyData
import com.example.ui.components.Clay3dSphere
import com.example.ui.components.EmptyStateView
import com.example.ui.components.MoneyFlowCard
import com.example.ui.components.SavingsGoalCard
import com.example.ui.theme.MoneyFlowTheme

@Composable
fun SavingsGoalsScreen(
    goals: List<SavingsGoalEntity>,
    currencyCode: String,
    onBackClick: () -> Unit,
    onAddGoalClick: () -> Unit,
    onAddMoney: (goalId: Long, amountMinor: Long) -> Unit,
    onWithdrawMoney: (goalId: Long, amountMinor: Long) -> Unit,
    onDeleteGoal: (SavingsGoalEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    var goalForMoneyAction by remember { mutableStateOf<Pair<SavingsGoalEntity, Boolean>?>(null) }
    var moneyAmountText by remember { mutableStateOf("") }
    var goalToDelete by remember { mutableStateOf<SavingsGoalEntity?>(null) }

    val curr = CurrencyData.getByCode(currencyCode)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
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
                                text = "Savings Goals",
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
                                text = "Track milestones & target funds",
                                style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // 3D Sphere Add Goal Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Clay3dSphere(
                            baseColor = Color(0xFFE89D86), // Soft Peach Clay Sphere
                            size = 46.dp
                        )
                        IconButton(onClick = onAddGoalClick) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Goal",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Goals List or Empty State
        if (goals.isEmpty()) {
            item {
                Box(modifier = Modifier.padding(24.dp)) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        EmptyStateView(
                            icon = Icons.Default.Savings,
                            title = "No savings goals",
                            description = "Create goals to save up for travel, gadgets, emergency funds, or big purchases.",
                            actionButtonText = "Create First Goal",
                            onActionClick = onAddGoalClick
                        )
                    }
                }
            }
        } else {
            items(goals, key = { it.id }) { goal ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    SavingsGoalCard(
                        goal = goal,
                        onAddMoneyClick = {
                            moneyAmountText = ""
                            goalForMoneyAction = Pair(goal, true)
                        },
                        onWithdrawMoneyClick = {
                            moneyAmountText = ""
                            goalForMoneyAction = Pair(goal, false)
                        },
                        onClick = { goalToDelete = goal }
                    )
                }
            }
        }
    }

    // Money Adjustment Dialog (Deposit / Withdraw)
    if (goalForMoneyAction != null) {
        val (goal, isDeposit) = goalForMoneyAction!!
        AlertDialog(
            onDismissRequest = { goalForMoneyAction = null },
            title = {
                Text(
                    text = if (isDeposit) "Add Money" else "Withdraw Money",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isDeposit) "Amount to deposit into '${goal.name}':" else "Amount to withdraw from '${goal.name}':",
                        style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = moneyAmountText,
                        onValueChange = { moneyAmountText = it },
                        label = { Text("Amount (${curr.symbol})") },
                        placeholder = { Text("0.00") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = CurrencyData.parseToMinorUnits(moneyAmountText, currencyCode)
                        if (parsed > 0) {
                            if (isDeposit) {
                                onAddMoney(goal.id, parsed)
                            } else {
                                onWithdrawMoney(goal.id, parsed)
                            }
                            goalForMoneyAction = null
                        }
                    }
                ) {
                    Text(if (isDeposit) "Deposit" else "Withdraw", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalForMoneyAction = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (goalToDelete != null) {
        val g = goalToDelete!!
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = {
                Text("Delete Goal?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Text("Do you want to delete '${g.name}'? Past transactions will remain safe.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteGoal(g)
                        goalToDelete = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFE26B58), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
