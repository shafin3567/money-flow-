package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

private fun createM3Light(mf: MoneyFlowColors) = lightColorScheme(
    primary = mf.primary,
    onPrimary = Color.White,
    primaryContainer = mf.primaryContainer,
    onPrimaryContainer = mf.textPrimary,
    secondary = mf.secondary,
    onSecondary = mf.textPrimary,
    secondaryContainer = mf.secondaryContainer,
    onSecondaryContainer = mf.textPrimary,
    tertiary = mf.warning,
    onTertiary = Color.White,
    background = mf.background,
    onBackground = mf.textPrimary,
    surface = mf.surface,
    onSurface = mf.textPrimary,
    surfaceVariant = mf.surfaceVariant,
    onSurfaceVariant = mf.textSecondary,
    outline = mf.inputBorder,
    outlineVariant = mf.border,
    error = mf.error,
    onError = Color.White,
    errorContainer = mf.errorContainer,
    onErrorContainer = mf.error
)

private fun createM3Dark(mf: MoneyFlowColors) = darkColorScheme(
    primary = mf.primary,
    onPrimary = Color.White,
    primaryContainer = mf.primaryContainer,
    onPrimaryContainer = mf.textPrimary,
    secondary = mf.secondary,
    onSecondary = Color(0xFF2E2218),
    secondaryContainer = mf.secondaryContainer,
    onSecondaryContainer = mf.textPrimary,
    tertiary = mf.warning,
    onTertiary = Color.Black,
    background = mf.background,
    onBackground = mf.textPrimary,
    surface = mf.surface,
    onSurface = mf.textPrimary,
    surfaceVariant = mf.surfaceVariant,
    onSurfaceVariant = mf.textSecondary,
    outline = mf.inputBorder,
    outlineVariant = mf.border,
    error = mf.error,
    onError = Color.White,
    errorContainer = mf.errorContainer,
    onErrorContainer = mf.error
)

@Composable
fun MoneyFlowTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val mfColors = if (isDark) DarkMoneyFlowColors else LightMoneyFlowColors
    val m3ColorScheme = if (isDark) createM3Dark(mfColors) else createM3Light(mfColors)

    // Window Insets / System Bar Icons adaptation
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                // When dark, light status bars should be false (white icons on dark status bar)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(
        LocalMoneyFlowColors provides mfColors
    ) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Backwards-compatible overload
@Composable
fun MoneyFlowTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MoneyFlowTheme(
        themeMode = if (darkTheme) AppThemeMode.DARK else AppThemeMode.LIGHT,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MoneyFlowTheme(
        themeMode = if (darkTheme) AppThemeMode.DARK else AppThemeMode.LIGHT,
        content = content
    )
}
