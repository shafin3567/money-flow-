package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

/**
 * 3D Clay Wave Landscape canvas component replicating the reference image's
 * smooth organic dunes (powder slate blue, warm ivory crest, and peach sand).
 */
@Composable
fun ClayWaveBackground(
    modifier: Modifier = Modifier,
    topSandColor: Color = Color(0xFFFBECE2),
    crestColor: Color = Color(0xFFFFFBF6),
    slateWaveColor: Color = Color(0xFF8FAFC6),
    accentPeachColor: Color = Color(0xFFF2C2B2),
    isDark: Boolean = false
) {
    val sand = if (isDark) Color(0xFF262121) else topSandColor
    val crest = if (isDark) Color(0xFF332B2C) else crestColor
    val wave = if (isDark) Color(0xFF4A6B85) else slateWaveColor
    val waveDark = if (isDark) Color(0xFF31495C) else Color(0xFF6D8FA7)

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Top Sand Base
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    sand,
                    sand.copy(alpha = 0.92f)
                ),
                startY = 0f,
                endY = h
            )
        )

        // 2. Middle Warm Ivory/Peach Wave Crest
        val crestPath = Path().apply {
            moveTo(0f, h * 0.45f)
            cubicTo(
                w * 0.25f, h * 0.70f,
                w * 0.65f, h * 0.35f,
                w, h * 0.60f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = crestPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    crest,
                    crest.copy(alpha = 0.95f)
                ),
                startY = h * 0.35f,
                endY = h
            )
        )

        // 3. Foreground Powder Slate Blue 3D Wave with smooth ridge
        val wavePath = Path().apply {
            moveTo(0f, h * 0.22f)
            cubicTo(
                w * 0.18f, h * 0.38f,
                w * 0.22f, h * 0.90f,
                w * 0.58f, h * 0.76f
            )
            cubicTo(
                w * 0.75f, h * 0.68f,
                w * 0.88f, h * 0.82f,
                w, h * 0.72f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        // Wave soft ambient shadow under the ridge
        val waveShadowPath = Path().apply {
            moveTo(0f, h * 0.24f)
            cubicTo(
                w * 0.18f, h * 0.40f,
                w * 0.22f, h * 0.92f,
                w * 0.58f, h * 0.78f
            )
            cubicTo(
                w * 0.75f, h * 0.70f,
                w * 0.88f, h * 0.84f,
                w, h * 0.74f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = waveShadowPath,
            color = Color(0x28000000)
        )

        // Draw the primary 3D slate wave
        drawPath(
            path = wavePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    wave,
                    waveDark
                ),
                startY = h * 0.22f,
                endY = h
            )
        )
    }
}
