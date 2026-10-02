package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 3D Clay Donut Gauge component matching the reference image.
 * Renders layered soft-tone arcs with rounded caps and a central 3D clay pearl sphere.
 */
@Composable
fun Clay3dDonut(
    primaryProgress: Float,
    secondaryProgress: Float = 0f,
    modifier: Modifier = Modifier,
    size: Dp = 110.dp,
    primaryColor: Color = Color(0xFFE59C88),   // Soft terracotta peach
    secondaryColor: Color = Color(0xFF8DAEC6), // Powder slate blue
    trackColor: Color = Color(0xFFF3E7DC),     // Warm sand track
    centerSphereColor: Color = Color(0xFFFFFDFC),
    strokeWidth: Dp = 16.dp,
    showCenterSphere: Boolean = true
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokePx = strokeWidth.toPx()
            val canvasSize = size.toPx()
            val arcSize = canvasSize - strokePx
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // 1. Background Track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // 2. Secondary Progress Arc (Blue)
            if (secondaryProgress > 0.01f) {
                val secSweep = (secondaryProgress.coerceIn(0f, 1f) * 360f)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            secondaryColor,
                            secondaryColor.copy(alpha = 0.85f),
                            secondaryColor
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = secSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }

            // 3. Primary Progress Arc (Peach)
            if (primaryProgress > 0.01f) {
                val start = if (secondaryProgress > 0.01f) -90f + (secondaryProgress * 360f) else -90f
                val sweep = (primaryProgress.coerceIn(0f, 1f) * 360f)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.85f),
                            primaryColor
                        )
                    ),
                    startAngle = start,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // 4. Central 3D Clay Pearl Sphere
        if (showCenterSphere) {
            Clay3dSphere(
                baseColor = centerSphereColor,
                size = size * 0.42f,
                shadowColor = Color(0x35000000)
            )
        }
    }
}
