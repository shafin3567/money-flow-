package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Photorealistic Soft 3D Clay Sphere matching the reference art style.
 * Features specular highlight, ambient gradient, and soft directional drop shadow.
 */
@Composable
fun Clay3dSphere(
    baseColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    highlightColor: Color = Color.White.copy(alpha = 0.85f),
    shadowColor: Color = Color(0x302D2526)
) {
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.toPx()
            val height = size.toPx()
            val radius = (width.coerceAtMost(height) / 2f) * 0.85f
            val center = Offset(width / 2f, height / 2f)

            // 1. Soft bottom-right ambient drop shadow
            val shadowCenter = Offset(center.x + radius * 0.18f, center.y + radius * 0.22f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        shadowColor,
                        shadowColor.copy(alpha = shadowColor.alpha * 0.4f),
                        Color.Transparent
                    ),
                    center = shadowCenter,
                    radius = radius * 1.15f
                ),
                radius = radius * 1.15f,
                center = shadowCenter
            )

            // 2. Base Sphere with 3D spherical gradient lighting
            // Darker rim/underside
            val darkBase = Color(
                red = (baseColor.red * 0.68f).coerceIn(0f, 1f),
                green = (baseColor.green * 0.68f).coerceIn(0f, 1f),
                blue = (baseColor.blue * 0.68f).coerceIn(0f, 1f),
                alpha = baseColor.alpha
            )
            val lightCenter = Offset(center.x - radius * 0.32f, center.y - radius * 0.35f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        baseColor,
                        baseColor,
                        darkBase
                    ),
                    center = lightCenter,
                    radius = radius * 1.35f
                ),
                radius = radius,
                center = center
            )

            // 3. Specular soft highlight on upper-left
            val highlightRadius = radius * 0.45f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        highlightColor,
                        highlightColor.copy(alpha = highlightColor.alpha * 0.3f),
                        Color.Transparent
                    ),
                    center = lightCenter,
                    radius = highlightRadius
                ),
                radius = highlightRadius,
                center = lightCenter
            )
        }
    }
}
