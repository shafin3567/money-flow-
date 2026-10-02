package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.model.CurrencyData
import com.example.ui.theme.MoneyFlowTheme
import com.example.ui.theme.SoftPeachAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavingsGoalCard(
    goal: SavingsGoalEntity,
    onAddMoneyClick: () -> Unit,
    onWithdrawMoneyClick: () -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    val currentFormatted = CurrencyData.formatMoney(goal.currentMinorUnits, goal.currencyCode)
    val targetFormatted = CurrencyData.formatMoney(goal.targetMinorUnits, goal.currencyCode)

    val progress = if (goal.targetMinorUnits > 0) {
        (goal.currentMinorUnits.toFloat() / goal.targetMinorUnits.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "goalProgress")

    val percent = if (goal.targetMinorUnits > 0) {
        ((goal.currentMinorUnits.toDouble() / goal.targetMinorUnits.toDouble()) * 100).toInt()
    } else 0

    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val targetDateStr = dateFormat.format(Date(goal.targetDateMillis))

    val accentColor = parseColorHex(goal.colorHex, SoftPeachAccent)
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
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3D Clay Donut Gauge with Center Sphere
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Clay3dDonut(
                    primaryProgress = animatedProgress,
                    size = 56.dp,
                    strokeWidth = 7.dp,
                    primaryColor = accentColor,
                    trackColor = if (colors.isDark) Color(0xFF383032) else Color(0xFFF3EBE0),
                    showCenterSphere = true
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = colors.textPrimary,
                        letterSpacing = (-0.2).sp,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 14.5.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$currentFormatted / $targetFormatted",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        fontSize = 12.5.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Target: $targetDateStr",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colors.textTertiary,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Quick add/withdraw 3D buttons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Withdraw (-)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (colors.isDark) Color(0xFF383032) else Color(0xFFF3EBE0))
                        .clickable(onClick = onWithdrawMoneyClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Withdraw from goal",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Add (+) with 3D Sphere aesthetic
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .clickable(onClick = onAddMoneyClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to goal",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
