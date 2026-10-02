package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyData
import com.example.data.repository.BudgetProgress
import com.example.ui.theme.MoneyFlowTheme

@Composable
fun BudgetProgressCard(
    budgetProgress: BudgetProgress,
    categoryName: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    val budget = budgetProgress.budget
    val spentFormatted = CurrencyData.formatMoney(budgetProgress.spentMinorUnits, budget.currencyCode)
    val limitFormatted = CurrencyData.formatMoney(budget.limitMinorUnits, budget.currencyCode)
    val remainingFormatted = CurrencyData.formatMoney(budgetProgress.remainingMinorUnits, budget.currencyCode)
    val dailyAllowanceFormatted = CurrencyData.formatMoney(budgetProgress.dailySpendingAllowanceMinorUnits, budget.currencyCode)

    val progress = budgetProgress.progressPercentage.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "budgetProgress")

    val progressColor = when {
        budgetProgress.progressPercentage >= 1.0f -> Color(0xFFE26B58)
        budgetProgress.progressPercentage >= 0.85f -> Color(0xFFEAA236)
        else -> Color(0xFF7FA7C4) // Powder Slate Blue
    }

    val percentageInt = (budgetProgress.progressPercentage * 100).toInt()
    val cardShape = RoundedCornerShape(24.dp)
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (colors.isDark) 3.dp else 5.dp,
                shape = cardShape,
                ambientColor = colors.shadow,
                spotColor = colors.shadow
            )
            .clip(cardShape)
            .background(if (colors.isDark) colors.surfaceElevated else Color.White)
            .then(clickModifier)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Budget name + percentage pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Clay3dSphere(
                        baseColor = progressColor,
                        size = 18.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = budget.name.ifBlank { categoryName },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontSize = 14.5.sp
                        ),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(progressColor.copy(alpha = 0.18f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$percentageInt%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = progressColor,
                            letterSpacing = 0.5.sp,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spent vs Limit text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$spentFormatted / $limitFormatted",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 13.5.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "$remainingFormatted left",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colors.textSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3D Thick Linear Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = if (colors.isDark) Color(0xFF383032) else Color(0xFFF3EBE0),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Daily allowance helper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${budgetProgress.daysRemainingInMonth} days left",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colors.textTertiary,
                        fontSize = 11.5.sp
                    ),
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = "~$dailyAllowanceFormatted / day",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = if (budgetProgress.remainingMinorUnits > 0) colors.textSecondary else colors.expense
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}
