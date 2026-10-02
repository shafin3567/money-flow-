package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.CurrencyData
import com.example.data.model.TransactionType
import com.example.ui.theme.MoneyFlowTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatTransactionDate(dateMillis: Long): String {
    val txCal = Calendar.getInstance().apply { timeInMillis = dateMillis }
    val nowCal = Calendar.getInstance()

    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val timeStr = timeFormat.format(Date(dateMillis))

    return if (txCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
        txCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)
    ) {
        "Today, $timeStr"
    } else if (txCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
        txCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR) - 1
    ) {
        "Yesterday, $timeStr"
    } else {
        val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        dateFormat.format(Date(dateMillis))
    }
}

@Composable
fun TransactionRow(
    transaction: TransactionEntity,
    category: CategoryEntity?,
    sourceAccount: AccountEntity?,
    destAccount: AccountEntity?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    val shape = RoundedCornerShape(22.dp)

    val isExpense = transaction.type == TransactionType.EXPENSE.name
    val isIncome = transaction.type == TransactionType.INCOME.name
    val isTransfer = transaction.type == TransactionType.TRANSFER.name

    val amountPrefix = when {
        isExpense -> "-"
        isIncome -> "+"
        else -> ""
    }

    val amountColor = when {
        isExpense -> colors.expense
        isIncome -> colors.income
        else -> colors.textPrimary
    }

    val title = when {
        transaction.note.isNotBlank() -> transaction.note
        category != null -> category.name
        isTransfer -> "Transfer"
        else -> "Transaction"
    }

    val subTitle = when {
        isTransfer -> {
            val fromName = sourceAccount?.name ?: "Account"
            val toName = destAccount?.name ?: "Account"
            "$fromName → $toName"
        }
        else -> {
            val catName = category?.name ?: "General"
            val accName = sourceAccount?.name ?: "Account"
            "$catName • $accName"
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (colors.isDark) 2.dp else 3.dp,
                shape = shape,
                ambientColor = colors.shadow,
                spotColor = colors.shadow
            )
            .clip(shape)
            .background(if (colors.isDark) colors.surfaceElevated else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3D Sphere Category Icon Badge
            if (isTransfer) {
                Box(
                    modifier = Modifier.size(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Clay3dSphere(
                        baseColor = Color(0xFF7FA7C4),
                        size = 42.dp
                    )
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                val iconName = category?.iconName ?: "attach_money"
                val colorHex = category?.colorHex ?: "#7FA7C4"
                CategoryIconBadge(
                    iconName = iconName,
                    colorHex = colorHex,
                    size = 42.dp,
                    iconSize = 18.dp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title and metadata
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 14.5.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$subTitle • ${formatTransactionDate(transaction.dateMillis)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colors.textTertiary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.5.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount
            val formattedAmount = CurrencyData.formatMoney(transaction.amountMinorUnits, transaction.currencyCode)
            Text(
                text = "$amountPrefix$formattedAmount",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = amountColor,
                    fontSize = 14.5.sp,
                    letterSpacing = (-0.2).sp
                ),
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
