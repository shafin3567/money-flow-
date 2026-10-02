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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MoneyFlowTheme

/**
 * Floating Clay Capsule matching the reference image's metric pill design.
 * Built with responsive text-fitting to strictly prevent awkward word wraps.
 */
@Composable
fun ClayPillCapsule(
    title: String,
    value: String,
    sphereColor: Color,
    modifier: Modifier = Modifier,
    sphereSize: Dp = 20.dp,
    containerColor: Color = Color.White,
    textColor: Color = Color(0xFF2D2526),
    subtextColor: Color = Color(0xFF7D6F70),
    onClick: (() -> Unit)? = null
) {
    val colors = MoneyFlowTheme.colors
    val shape = RoundedCornerShape(22.dp)
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Box(
        modifier = modifier
            .shadow(
                elevation = if (colors.isDark) 3.dp else 5.dp,
                shape = shape,
                ambientColor = colors.shadow,
                spotColor = colors.shadow
            )
            .clip(shape)
            .background(if (colors.isDark) colors.surfaceElevated else containerColor)
            .then(clickModifier)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 3D Sphere on the left
            Clay3dSphere(
                baseColor = sphereColor,
                size = sphereSize
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (colors.isDark) colors.textSecondary else subtextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        letterSpacing = 0.3.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 13.5.sp,
                        letterSpacing = (-0.2).sp,
                        color = if (colors.isDark) colors.textPrimary else textColor
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
