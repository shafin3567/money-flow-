package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class MoneyFlowColors(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceSubtle: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimary: Color,
    val secondary: Color,
    val secondaryContainer: Color,
    val income: Color,
    val incomeContainer: Color,
    val expense: Color,
    val expenseContainer: Color,
    val transfer: Color,
    val transferContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val error: Color,
    val errorContainer: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val border: Color,
    val shadow: Color,
    val divider: Color,
    val isDark: Boolean,
    val inputBackground: Color,
    val inputText: Color,
    val inputPlaceholder: Color,
    val inputLabel: Color,
    val inputFocusedLabel: Color,
    val inputUnfocusedLabel: Color,
    val inputCursor: Color,
    val inputIcon: Color,
    val inputBorder: Color,
    val inputFocusedBorder: Color,
    val inputError: Color,
    val inputSupportingText: Color,
    val inputSelectionBackground: Color,
    val inputSelectionHandle: Color
)

val LightMoneyFlowColors = MoneyFlowColors(
    background = Color(0xFFFCF9F4),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceSubtle = Color(0xFFF6F3EE),
    surfaceVariant = Color(0xFFE5E2DD),
    primary = Color(0xFF42634D),
    primaryContainer = Color(0xFF5A7C65),
    onPrimary = Color.White,
    secondary = Color(0xFF8D4E2F),
    secondaryContainer = Color(0xFFFDAB85),
    income = Color(0xFF42634D),
    incomeContainer = Color(0xFFC6ECD0),
    expense = Color(0xFFBA1A1A),
    expenseContainer = Color(0xFFFFDAD6),
    transfer = Color(0xFF325F86),
    transferContainer = Color(0xFFCEE5FF),
    warning = Color(0xFF8D4E2F),
    warningContainer = Color(0xFFFFDBCC),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    textPrimary = Color(0xFF1C1C19),
    textSecondary = Color(0xFF424843),
    textTertiary = Color(0xFF727972),
    textDisabled = Color(0xFFC2C8C1),
    border = Color(0x1FC2C8C1),
    shadow = Color(0x22181C19),
    divider = Color(0x181C1C19),
    isDark = false,
    inputBackground = Color(0xFFF0EDE9),
    inputText = Color(0xFF1C1C19),
    inputPlaceholder = Color(0xFF727972),
    inputLabel = Color(0xFF424843),
    inputFocusedLabel = Color(0xFF42634D),
    inputUnfocusedLabel = Color(0xFF727972),
    inputCursor = Color(0xFF42634D),
    inputIcon = Color(0xFF424843),
    inputBorder = Color(0x20727972),
    inputFocusedBorder = Color(0xFF5A7C65),
    inputError = Color(0xFFBA1A1A),
    inputSupportingText = Color(0xFF727972),
    inputSelectionBackground = Color(0x405A7C65),
    inputSelectionHandle = Color(0xFF42634D)
)

val DarkMoneyFlowColors = MoneyFlowColors(
    background = Color(0xFF000000), // Pure OLED dark
    surface = Color(0xFF121413),
    surfaceElevated = Color(0xFF1C1E1D),
    surfaceSubtle = Color(0xFF242725),
    surfaceVariant = Color(0xFF313532),
    primary = Color(0xFFABCFB5),
    primaryContainer = Color(0xFF2D4E39),
    onPrimary = Color(0xFF002110),
    secondary = Color(0xFFFFB695),
    secondaryContainer = Color(0xFF70371A),
    income = Color(0xFFABCFB5),
    incomeContainer = Color(0xFF2D4E39),
    expense = Color(0xFFFFB4AB),
    expenseContainer = Color(0xFF93000A),
    transfer = Color(0xFF9FCBF6),
    transferContainer = Color(0xFF194A6F),
    warning = Color(0xFFFFB695),
    warningContainer = Color(0xFF70371A),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    textPrimary = Color(0xFFF3F0EB),
    textSecondary = Color(0xFFC2C8C1),
    textTertiary = Color(0xFF8C928B),
    textDisabled = Color(0xFF424843),
    border = Color(0x33FFFFFF),
    shadow = Color(0x80000000),
    divider = Color(0x22FFFFFF),
    isDark = true,
    inputBackground = Color(0xFF1C1E1D),
    inputText = Color(0xFFF3F0EB),
    inputPlaceholder = Color(0xFF8C928B),
    inputLabel = Color(0xFFC2C8C1),
    inputFocusedLabel = Color(0xFFABCFB5),
    inputUnfocusedLabel = Color(0xFF8C928B),
    inputCursor = Color(0xFFABCFB5),
    inputIcon = Color(0xFFC2C8C1),
    inputBorder = Color(0x33FFFFFF),
    inputFocusedBorder = Color(0xFFABCFB5),
    inputError = Color(0xFFFFB4AB),
    inputSupportingText = Color(0xFF8C928B),
    inputSelectionBackground = Color(0x40ABCFB5),
    inputSelectionHandle = Color(0xFFABCFB5)
)

object MoneyFlowShapes {
    val small = RoundedCornerShape(12.dp)
    val medium = RoundedCornerShape(20.dp)
    val large = RoundedCornerShape(28.dp)
    val extraLarge = RoundedCornerShape(36.dp)
    val pill = RoundedCornerShape(50)
}

object MoneyFlowSpacing {
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val s: Dp = 8.dp
    val m: Dp = 12.dp
    val l: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
}

val LocalMoneyFlowColors = staticCompositionLocalOf { LightMoneyFlowColors }

object MoneyFlowTheme {
    val colors: MoneyFlowColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMoneyFlowColors.current

    val shapes: MoneyFlowShapes
        get() = MoneyFlowShapes

    val spacing: MoneyFlowSpacing
        get() = MoneyFlowSpacing
}
