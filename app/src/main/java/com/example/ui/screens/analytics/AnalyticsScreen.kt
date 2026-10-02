package com.example.ui.screens.analytics

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.CurrencyData
import com.example.data.model.InsightItem
import com.example.data.model.InsightType
import com.example.data.model.TransactionType
import com.example.ui.components.CategoryIconBadge
import com.example.ui.components.Clay3dDonut
import com.example.ui.components.Clay3dSphere
import com.example.ui.components.MoneyFlowCard
import com.example.ui.theme.MoneyFlowTheme
import java.util.Calendar

data class CategorySpendItem(
    val category: CategoryEntity?,
    val spentMinorUnits: Long,
    val percentage: Float
)

@Composable
fun AnalyticsScreen(
    transactions: List<TransactionEntity>,
    categories: List<CategoryEntity>,
    insights: List<InsightItem>,
    currencyCode: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors

    // Current month calculations
    val cal = Calendar.getInstance()
    val curYear = cal.get(Calendar.YEAR)
    val curMonth = cal.get(Calendar.MONTH)

    val currentMonthTx = remember(transactions) {
        val txCal = Calendar.getInstance()
        transactions.filter {
            txCal.timeInMillis = it.dateMillis
            txCal.get(Calendar.YEAR) == curYear && txCal.get(Calendar.MONTH) == curMonth
        }
    }

    val totalIncome = remember(currentMonthTx) {
        currentMonthTx.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amountMinorUnits }
    }

    val totalExpense = remember(currentMonthTx) {
        currentMonthTx.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amountMinorUnits }
    }

    val netSavings = totalIncome - totalExpense

    // Category breakdown
    val categoryBreakdown = remember(currentMonthTx, categories, totalExpense) {
        val expenses = currentMonthTx.filter { it.type == TransactionType.EXPENSE.name }
        val byCat = expenses.groupBy { it.categoryId }
        byCat.map { (catId, txs) ->
            val cat = categories.find { it.id == catId }
            val sum = txs.sumOf { it.amountMinorUnits }
            val pct = if (totalExpense > 0) sum.toFloat() / totalExpense.toFloat() else 0f
            CategorySpendItem(cat, sum, pct)
        }.sortedByDescending { it.spentMinorUnits }
    }

    val totalFlow = (totalIncome + totalExpense).coerceAtLeast(1L)
    val incomeRatio = (totalIncome.toFloat() / totalFlow.toFloat()).coerceIn(0f, 1f)
    val expenseRatio = (totalExpense.toFloat() / totalFlow.toFloat()).coerceIn(0f, 1f)

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
                    verticalAlignment = Alignment.CenterVertically
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

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Spending Analytics",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = colors.textPrimary,
                                letterSpacing = (-0.5).sp,
                                fontSize = 26.sp
                            ),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "This Month's Financial Health",
                            style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 3D Donut Visual Flow & Summary Card
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                MoneyFlowCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "MONTHLY CASH FLOW",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.3.sp,
                                color = colors.textTertiary
                            ),
                            maxLines = 1,
                            softWrap = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 3D Clay Donut Chart
                            Clay3dDonut(
                                primaryProgress = expenseRatio,
                                secondaryProgress = incomeRatio,
                                primaryColor = Color(0xFFE26B58),   // Peach Coral
                                secondaryColor = Color(0xFF7FA7C4), // Powder Slate Blue
                                trackColor = if (colors.isDark) Color(0xFF383032) else Color(0xFFF3EBE0),
                                size = 94.dp,
                                strokeWidth = 13.dp,
                                showCenterSphere = true
                            )

                            Spacer(modifier = Modifier.width(18.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                CashFlowStat(
                                    label = "Income",
                                    amount = CurrencyData.formatMoney(totalIncome, currencyCode),
                                    color = Color(0xFF43A047),
                                    sphereColor = Color(0xFF7FA7C4)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CashFlowStat(
                                    label = "Expenses",
                                    amount = CurrencyData.formatMoney(totalExpense, currencyCode),
                                    color = Color(0xFFE26B58),
                                    sphereColor = Color(0xFFE26B58)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CashFlowStat(
                                    label = "Net Savings",
                                    amount = CurrencyData.formatMoney(netSavings, currencyCode),
                                    color = if (netSavings >= 0) Color(0xFF43A047) else Color(0xFFE26B58),
                                    sphereColor = Color(0xFFFBE4C6)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Local Rule-Based Insights Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "LOCAL INSIGHTS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp,
                        color = colors.textTertiary
                    ),
                    maxLines = 1,
                    softWrap = false
                )

                Spacer(modifier = Modifier.height(10.dp))

                insights.forEach { insight ->
                    val bg = when (insight.type) {
                        InsightType.POSITIVE -> if (colors.isDark) Color(0xFF1E3324) else Color(0xFFEAF5EC)
                        InsightType.WARNING -> if (colors.isDark) Color(0xFF3B2D18) else Color(0xFFFFF7E3)
                        InsightType.NEUTRAL -> if (colors.isDark) Color(0xFF263745) else Color(0xFFD4E5F2)
                    }
                    val iconTint = when (insight.type) {
                        InsightType.POSITIVE -> Color(0xFF43A047)
                        InsightType.WARNING -> Color(0xFFEAA236)
                        InsightType.NEUTRAL -> Color(0xFF7FA7C4)
                    }
                    val iconVector = when (insight.iconName) {
                        "trending_up" -> Icons.Default.TrendingUp
                        "savings" -> Icons.Default.Savings
                        "pie_chart" -> Icons.Default.PieChart
                        "lock" -> Icons.Default.Lock
                        else -> Icons.Default.Info
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(bg)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = insight.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontSize = 15.sp
                                    ),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = insight.description,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = colors.textSecondary,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Spending by Category
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "SPENDING BY CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp,
                        color = colors.textTertiary
                    ),
                    maxLines = 1,
                    softWrap = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (categoryBreakdown.isEmpty()) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        Text(
                            text = "No expenses recorded this month yet.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
                        )
                    }
                } else {
                    categoryBreakdown.forEach { item ->
                        val catName = item.category?.name ?: "General"
                        val iconName = item.category?.iconName ?: "attach_money"
                        val colorHex = item.category?.colorHex ?: "#7FA7C4"
                        val percentInt = (item.percentage * 100).toInt()
                        val formatted = CurrencyData.formatMoney(item.spentMinorUnits, currencyCode)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CategoryIconBadge(
                                            iconName = iconName,
                                            colorHex = colorHex,
                                            size = 40.dp,
                                            iconSize = 18.dp
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = catName,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = colors.textPrimary,
                                                    fontSize = 15.sp
                                                ),
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "$percentInt% of total expenses",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = colors.textTertiary,
                                                    fontSize = 11.5.sp
                                                ),
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = formatted,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = colors.textPrimary,
                                                fontSize = 15.sp
                                            ),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    LinearProgressIndicator(
                                        progress = { item.percentage },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(7.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = Color(0xFF7FA7C4),
                                        trackColor = if (colors.isDark) Color(0xFF383032) else Color(0xFFF3EBE0),
                                        strokeCap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CashFlowStat(
    label: String,
    amount: String,
    color: Color,
    sphereColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Clay3dSphere(
            baseColor = sphereColor,
            size = 14.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MoneyFlowTheme.colors.textSecondary,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                softWrap = false
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 14.5.sp
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
