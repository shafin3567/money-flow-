package com.example.ui.screens.home

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.CurrencyData
import com.example.data.repository.BudgetProgress
import com.example.ui.components.BudgetProgressCard
import com.example.ui.components.Clay3dSphere
import com.example.ui.components.EmptyStateView
import com.example.ui.components.MoneyBalanceCard
import com.example.ui.components.MoneyFlowCard
import com.example.ui.components.QuickActionButton
import com.example.ui.components.SavingsGoalCard
import com.example.ui.components.TransactionRow
import com.example.ui.theme.MoneyFlowTheme
import java.util.Calendar

@Composable
fun HomeScreen(
    totalBalanceMinorUnits: Long,
    monthIncomeMinorUnits: Long,
    monthExpenseMinorUnits: Long,
    currencyCode: String,
    recentTransactions: List<TransactionEntity>,
    categories: List<CategoryEntity>,
    accounts: List<AccountEntity>,
    budgets: List<BudgetProgress>,
    savingsGoals: List<SavingsGoalEntity>,
    onAddExpenseClick: () -> Unit,
    onAddIncomeClick: () -> Unit,
    onTransferClick: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onSeeAllTransactionsClick: () -> Unit,
    onSeeAllBudgetsClick: () -> Unit,
    onSeeAllGoalsClick: () -> Unit,
    onAddMoneyToGoal: (goalId: Long, amountMinor: Long) -> Unit,
    onWithdrawMoneyFromGoal: (goalId: Long, amountMinor: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    var goalForMoneyAction by remember { mutableStateOf<Pair<SavingsGoalEntity, Boolean>?>(null) }
    var moneyAmountText by remember { mutableStateOf("") }
    val curr = CurrencyData.getByCode(currencyCode)

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..22 -> "Good evening"
            else -> "Good night"
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Greeting Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 22.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = colors.textSecondary,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "MoneyFlow",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = colors.textPrimary,
                                letterSpacing = (-0.5).sp,
                                fontSize = 28.sp
                            ),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 3D Pastel Avatar Emblem
                    Box(
                        modifier = Modifier.size(46.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Clay3dSphere(
                            baseColor = Color(0xFF7FA7C4),
                            size = 46.dp
                        )
                        Text(
                            text = "MF",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // Hero 3D Clay Wave Balance Card
        item {
            Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                MoneyBalanceCard(
                    totalBalanceMinorUnits = totalBalanceMinorUnits,
                    monthIncomeMinorUnits = monthIncomeMinorUnits,
                    monthExpenseMinorUnits = monthExpenseMinorUnits,
                    currencyCode = currencyCode
                )
            }
        }

        // Quick Action Pills
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Text(
                    text = stringResource(R.string.quick_actions).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp,
                        color = colors.textSecondary
                    ),
                    maxLines = 1,
                    softWrap = false
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        label = "Expense",
                        icon = Icons.Default.ArrowUpward,
                        containerColor = Color.White,
                        iconTint = Color(0xFFE26B58), // Coral sphere
                        onClick = onAddExpenseClick,
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionButton(
                        label = "Income",
                        icon = Icons.Default.ArrowDownward,
                        containerColor = Color.White,
                        iconTint = Color(0xFF5BA678), // Mint green sphere
                        onClick = onAddIncomeClick,
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionButton(
                        label = "Transfer",
                        icon = Icons.Default.SwapHoriz,
                        containerColor = Color.White,
                        iconTint = Color(0xFF7FA7C4), // Slate blue sphere
                        onClick = onTransferClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Budget Overview
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.budget_overview),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.3.sp,
                            color = colors.textSecondary
                        ),
                        maxLines = 1,
                        softWrap = false
                    )

                    if (budgets.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.see_all),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF7FA7C4),
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.clickable(onClick = onSeeAllBudgetsClick)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (budgets.isEmpty()) {
                    MoneyFlowCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp
                    ) {
                        Text(
                            text = "No budgets configured yet. Tap 'Budgets' in the bottom navigation to set monthly limits.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
                        )
                    }
                } else {
                    budgets.take(3).forEach { bp ->
                        val catName = categories.find { it.id == bp.budget.categoryId }?.name ?: "Overall Budget"
                        BudgetProgressCard(
                            budgetProgress = bp,
                            categoryName = catName,
                            onClick = onSeeAllBudgetsClick
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Savings Goals
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.savings_goals),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.3.sp,
                            color = colors.textSecondary
                        ),
                        maxLines = 1,
                        softWrap = false
                    )

                    if (savingsGoals.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.see_all),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF7FA7C4),
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.clickable(onClick = onSeeAllGoalsClick)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (savingsGoals.isEmpty()) {
                    MoneyFlowCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp
                    ) {
                        Text(
                            text = "No savings goals created yet. Set a target for vacation, gadgets, or emergency fund.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
                        )
                    }
                } else {
                    savingsGoals.take(2).forEach { goal ->
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
                            onClick = onSeeAllGoalsClick
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Recent Transactions Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recent_transactions),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.3.sp,
                            color = colors.textSecondary
                        ),
                        maxLines = 1,
                        softWrap = false
                    )

                    if (recentTransactions.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.see_all),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF7FA7C4),
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.clickable(onClick = onSeeAllTransactionsClick)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Recent Transactions List
        if (recentTransactions.isEmpty()) {
            item {
                Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        EmptyStateView(
                            icon = Icons.Default.ReceiptLong,
                            title = stringResource(R.string.no_transactions_title),
                            description = stringResource(R.string.no_transactions_desc),
                            actionButtonText = stringResource(R.string.add_transaction),
                            onActionClick = onAddExpenseClick
                        )
                    }
                }
            }
        } else {
            items(recentTransactions, key = { it.id }) { tx ->
                val category = categories.find { it.id == tx.categoryId }
                val sourceAcc = accounts.find { it.id == tx.accountId }
                val destAcc = tx.toAccountId?.let { toId -> accounts.find { it.id == toId } }

                Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    TransactionRow(
                        transaction = tx,
                        category = category,
                        sourceAccount = sourceAcc,
                        destAccount = destAcc,
                        onClick = { onTransactionClick(tx) }
                    )
                }
            }
        }
    }

    // Money Adjustment Dialog (Deposit / Withdraw from HomeScreen)
    if (goalForMoneyAction != null) {
        val (goal, isDeposit) = goalForMoneyAction!!
        AlertDialog(
            onDismissRequest = { goalForMoneyAction = null },
            title = {
                Text(
                    text = if (isDeposit) "Add Money to ${goal.name}" else "Withdraw from ${goal.name}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isDeposit) "Enter the amount you saved:" else "Enter the amount to withdraw:",
                        style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = moneyAmountText,
                        onValueChange = { moneyAmountText = it },
                        label = { Text("Amount (${curr.symbol})") },
                        placeholder = { Text("e.g. 50.00") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = CurrencyData.parseToMinorUnits(moneyAmountText)
                        if (parsed != null && parsed > 0) {
                            if (isDeposit) {
                                onAddMoneyToGoal(goal.id, parsed)
                            } else {
                                onWithdrawMoneyFromGoal(goal.id, parsed)
                            }
                            goalForMoneyAction = null
                        }
                    }
                ) {
                    Text(if (isDeposit) "Add" else "Withdraw", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalForMoneyAction = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
