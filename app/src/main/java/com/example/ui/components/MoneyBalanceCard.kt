package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CurrencyData
import com.example.ui.theme.MoneyFlowTheme

@Composable
fun MoneyBalanceCard(
    totalBalanceMinorUnits: Long,
    monthIncomeMinorUnits: Long,
    monthExpenseMinorUnits: Long,
    currencyCode: String,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    val cardShape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (colors.isDark) 5.dp else 8.dp,
                shape = cardShape,
                ambientColor = colors.shadow,
                spotColor = colors.shadow
            )
            .clip(cardShape)
    ) {
        // 1. 3D Clay Wave Landscape Background
        ClayWaveBackground(
            isDark = colors.isDark,
            modifier = Modifier.matchParentSize()
        )

        // 2. Decorative Floating 3D Spheres from the reference art
        Clay3dSphere(
            baseColor = Color(0xFFFBE4C6), // warm yellow sphere
            size = 20.dp,
            modifier = Modifier.offset(x = 24.dp, y = 20.dp)
        )
        Clay3dSphere(
            baseColor = Color(0xFFFFFDF8), // pearl white sphere
            size = 36.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-24).dp, y = 18.dp)
        )
        Clay3dSphere(
            baseColor = Color(0xFFE89D86), // soft peach sphere
            size = 26.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-20).dp)
        )

        // 3. Card Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            // Label
            Text(
                text = stringResource(R.string.total_balance),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    color = if (colors.isDark) Color(0xFFB5A9A7) else Color(0xFF786A6B)
                ),
                maxLines = 1,
                softWrap = false
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main Balance Number
            AnimatedContent(
                targetState = totalBalanceMinorUnits,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "totalBalanceAnim"
            ) { targetBalance ->
                Text(
                    text = CurrencyData.formatMoney(targetBalance, currencyCode),
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = if (colors.isDark) Color(0xFFF7F2EE) else Color(0xFF2B2324),
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        fontSize = 30.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Month Income & Expense Floating Capsules matching the reference
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Income Floating Pill
                ClayPillCapsule(
                    title = "Income",
                    value = CurrencyData.formatMoney(monthIncomeMinorUnits, currencyCode),
                    sphereColor = Color(0xFFF7CB78), // Golden Clay Sphere
                    modifier = Modifier.weight(1f)
                )

                // Expense Floating Pill
                ClayPillCapsule(
                    title = "Expenses",
                    value = CurrencyData.formatMoney(monthExpenseMinorUnits, currencyCode),
                    sphereColor = Color(0xFFE58774), // Coral Peach Clay Sphere
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
