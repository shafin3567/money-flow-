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
    val isDark: Boolean
)

val LightMoneyFlowColors = MoneyFlowColors(
    background = Color(0xFFFAF4ED),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFDFB),
    surfaceSubtle = Color(0xFFF5ECE2),
    surfaceVariant = Color(0xFFEFE4D7),
    primary = Color(0xFF7FA7C4),
    primaryContainer = Color(0xFFD4E5F2),
    onPrimary = Color.White,
    secondary = Color(0xFFE89D86),
    secondaryContainer = Color(0xFFFCE8DE),
    income = Color(0xFF43A047),
    incomeContainer = Color(0xFFEAF5EC),
    expense = Color(0xFFE26B58),
    expenseContainer = Color(0xFFFDECE9),
    transfer = Color(0xFF7FA7C4),
    transferContainer = Color(0xFFD4E5F2),
    warning = Color(0xFFEAA236),
    warningContainer = Color(0xFFFFF7E3),
    error = Color(0xFFE26B58),
    errorContainer = Color(0xFFFDECE9),
    textPrimary = Color(0xFF2C2425),
    textSecondary = Color(0xFF736566),
    textTertiary = Color(0xFF9A8B8C),
    textDisabled = Color(0xFFB5A8A9),
    border = Color(0x142C2425),
    shadow = Color(0x1A3A2E2C),
    divider = Color(0x102C2425),
    isDark = false
)

val DarkMoneyFlowColors = MoneyFlowColors(
    background = Color(0xFF191516),
    surface = Color(0xFF241F20),
    surfaceElevated = Color(0xFF2D2628),
    surfaceSubtle = Color(0xFF383032),
    surfaceVariant = Color(0xFF443B3D),
    primary = Color(0xFF8FAFC6),
    primaryContainer = Color(0xFF263745),
    onPrimary = Color(0xFF191516),
    secondary = Color(0xFFF0AF9E),
    secondaryContainer = Color(0xFF452C26),
    income = Color(0xFF5ABF77),
    incomeContainer = Color(0xFF1C3826),
    expense = Color(0xFFEA7E6D),
    expenseContainer = Color(0xFF44221E),
    transfer = Color(0xFF8FAFC6),
    transferContainer = Color(0xFF263745),
    warning = Color(0xFFF2B855),
    warningContainer = Color(0xFF423218),
    error = Color(0xFFEA7E6D),
    errorContainer = Color(0xFF44221E),
    textPrimary = Color(0xFFF6F0EC),
    textSecondary = Color(0xFFB8ACA8),
    textTertiary = Color(0xFF8B807D),
    textDisabled = Color(0xFF5A5250),
    border = Color(0x22FFFFFF),
    shadow = Color(0x40000000),
    divider = Color(0x1AFFFFFF),
    isDark = true
)

object MoneyFlowShapes {
    val small = RoundedCornerShape(14.dp)
    val medium = RoundedCornerShape(20.dp)
    val large = RoundedCornerShape(28.dp)
    val extraLarge = RoundedCornerShape(34.dp)
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
