package com.example.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.example.ui.components.MoneyFlowTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.components.Clay3dSphere
import com.example.ui.components.EmptyStateView
import com.example.ui.components.MoneyFlowCard
import com.example.ui.components.TransactionRow
import com.example.ui.theme.MoneyFlowTheme
import com.example.ui.viewmodel.TransactionsFilterState
import java.util.Calendar

enum class TimeGroup {
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    EARLIER
}

fun groupTransactions(transactions: List<TransactionEntity>): Map<TimeGroup, List<TransactionEntity>> {
    val now = Calendar.getInstance()
    val todayYear = now.get(Calendar.YEAR)
    val todayDay = now.get(Calendar.DAY_OF_YEAR)

    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val yYear = yesterday.get(Calendar.YEAR)
    val yDay = yesterday.get(Calendar.DAY_OF_YEAR)

    val weekStart = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis

    val grouped = mutableMapOf<TimeGroup, MutableList<TransactionEntity>>()
    grouped[TimeGroup.TODAY] = mutableListOf()
    grouped[TimeGroup.YESTERDAY] = mutableListOf()
    grouped[TimeGroup.THIS_WEEK] = mutableListOf()
    grouped[TimeGroup.EARLIER] = mutableListOf()

    val txCal = Calendar.getInstance()
    for (tx in transactions) {
        txCal.timeInMillis = tx.dateMillis
        val tYear = txCal.get(Calendar.YEAR)
        val tDay = txCal.get(Calendar.DAY_OF_YEAR)

        when {
            tYear == todayYear && tDay == todayDay -> grouped[TimeGroup.TODAY]?.add(tx)
            tYear == yYear && tDay == yDay -> grouped[TimeGroup.YESTERDAY]?.add(tx)
            tx.dateMillis >= weekStart -> grouped[TimeGroup.THIS_WEEK]?.add(tx)
            else -> grouped[TimeGroup.EARLIER]?.add(tx)
        }
    }
    return grouped
}

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    categories: List<CategoryEntity>,
    accounts: List<AccountEntity>,
    filterState: TransactionsFilterState,
    onSearchQueryChange: (String) -> Unit,
    onTypeFilterChange: (String?) -> Unit,
    onCategoryFilterChange: (Long?) -> Unit,
    onAccountFilterChange: (Long?) -> Unit,
    onClearFilters: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onAddTransactionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    val groupedTransactions = remember(transactions) {
        groupTransactions(transactions)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Title & Search Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = colors.textPrimary,
                        letterSpacing = (-0.5).sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar with Claymorphic Container
                MoneyFlowTextField(
                    value = filterState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "Search note, merchant...",
                            maxLines = 1,
                            softWrap = false
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = colors.inputIcon
                        )
                    },
                    trailingIcon = {
                        if (filterState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = colors.inputIcon,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (colors.isDark) 2.dp else 4.dp,
                            shape = RoundedCornerShape(22.dp),
                            ambientColor = colors.shadow,
                            spotColor = colors.shadow
                        ),
                    shape = RoundedCornerShape(22.dp),
                    containerColor = colors.inputBackground,
                    focusedBorderColor = colors.inputFocusedBorder,
                    unfocusedBorderColor = colors.inputBorder,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Horizontal Filter Chips Row (All, Expense, Income, Transfer)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        label = "All",
                        isSelected = filterState.typeFilter == null,
                        onClick = { onTypeFilterChange(null) }
                    )
                    FilterChip(
                        label = "Expenses",
                        isSelected = filterState.typeFilter == TransactionType.EXPENSE.name,
                        activeColor = Color(0xFFE26B58),
                        onClick = { onTypeFilterChange(TransactionType.EXPENSE.name) }
                    )
                    FilterChip(
                        label = "Income",
                        isSelected = filterState.typeFilter == TransactionType.INCOME.name,
                        activeColor = Color(0xFF43A047),
                        onClick = { onTypeFilterChange(TransactionType.INCOME.name) }
                    )
                    FilterChip(
                        label = "Transfers",
                        isSelected = filterState.typeFilter == TransactionType.TRANSFER.name,
                        activeColor = Color(0xFF7FA7C4),
                        onClick = { onTypeFilterChange(TransactionType.TRANSFER.name) }
                    )

                    // Account filters if accounts exist
                    accounts.forEach { acc ->
                        val isSelected = filterState.accountIdFilter == acc.id
                        FilterChip(
                            label = acc.name,
                            isSelected = isSelected,
                            onClick = { onAccountFilterChange(if (isSelected) null else acc.id) }
                        )
                    }
                }
            }
        }

        // Empty State or Grouped Feed
        if (transactions.isEmpty()) {
            item {
                Box(modifier = Modifier.padding(24.dp)) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        EmptyStateView(
                            icon = Icons.Default.ReceiptLong,
                            title = if (filterState.searchQuery.isNotBlank() || filterState.typeFilter != null) "No matching transactions" else "No transactions yet",
                            description = if (filterState.searchQuery.isNotBlank() || filterState.typeFilter != null) "Try changing your search terms or filters." else "Add your first transaction to start understanding your spending.",
                            actionButtonText = if (filterState.searchQuery.isNotBlank() || filterState.typeFilter != null) "Reset Filters" else "Add Transaction",
                            onActionClick = {
                                if (filterState.searchQuery.isNotBlank() || filterState.typeFilter != null) {
                                    onClearFilters()
                                } else {
                                    onAddTransactionClick()
                                }
                            }
                        )
                    }
                }
            }
        } else {
            val groups = listOf(
                TimeGroup.TODAY to "TODAY",
                TimeGroup.YESTERDAY to "YESTERDAY",
                TimeGroup.THIS_WEEK to "THIS WEEK",
                TimeGroup.EARLIER to "EARLIER"
            )

            groups.forEach { (groupKey, headerTitle) ->
                val txsInGroup = groupedTransactions[groupKey] ?: emptyList()
                if (txsInGroup.isNotEmpty()) {
                    item {
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.3.sp,
                                color = colors.textTertiary
                            ),
                            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }

                    items(txsInGroup, key = { it.id }) { tx ->
                        val category = categories.find { it.id == tx.categoryId }
                        val sourceAcc = accounts.find { it.id == tx.accountId }
                        val destAcc = tx.toAccountId?.let { toId -> accounts.find { it.id == toId } }

                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
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
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    activeColor: Color = Color(0xFF7FA7C4),
    onClick: () -> Unit
) {
    val colors = MoneyFlowTheme.colors
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .shadow(
                elevation = if (isSelected) 3.dp else 1.dp,
                shape = shape,
                ambientColor = colors.shadow,
                spotColor = colors.shadow
            )
            .clip(shape)
            .background(if (isSelected) activeColor else if (colors.isDark) colors.surfaceElevated else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else colors.textPrimary
            ),
            maxLines = 1,
            softWrap = false
        )
    }
}
