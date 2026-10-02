package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun getCategoryIcon(name: String): ImageVector {
    return when (name.lowercase()) {
        "restaurant", "food", "food & dining" -> Icons.Default.Restaurant
        "shopping_cart", "groceries" -> Icons.Default.ShoppingCart
        "shopping_bag", "shopping" -> Icons.Default.ShoppingBag
        "directions_car", "transport" -> Icons.Default.DirectionsCar
        "home", "housing", "rent" -> Icons.Default.Home
        "receipt_long", "bills", "utilities" -> Icons.Default.ReceiptLong
        "movie", "entertainment" -> Icons.Default.Movie
        "medical_services", "health", "hospital" -> Icons.Default.LocalHospital
        "spa", "personal care" -> Icons.Default.Spa
        "school", "education" -> Icons.Default.School
        "flight", "travel" -> Icons.Default.Flight
        "payments", "salary" -> Icons.Default.Payments
        "work", "freelance", "business" -> Icons.Default.Work
        "trending_up", "investment", "investments" -> Icons.Default.TrendingUp
        "card_giftcard", "gift", "gifts" -> Icons.Default.CardGiftcard
        "account_balance", "bank" -> Icons.Default.AccountBalance
        "account_balance_wallet", "wallet" -> Icons.Default.AccountBalanceWallet
        "credit_card" -> Icons.Default.CreditCard
        "savings" -> Icons.Default.Savings
        else -> Icons.Default.MonetizationOn
    }
}

fun parseColorHex(hex: String, defaultColor: Color = Color(0xFFF7C59F)): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) {
            Color(0xFF000000 or colorInt)
        } else if (clean.length == 8) {
            Color(colorInt)
        } else {
            defaultColor
        }
    } catch (_: Exception) {
        defaultColor
    }
}

@Composable
fun CategoryIconBadge(
    iconName: String,
    colorHex: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 22.dp
) {
    val baseColor = parseColorHex(colorHex)
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // 3D Sphere Base
        Clay3dSphere(
            baseColor = baseColor,
            size = size
        )
        // Icon overlay with white tint
        Icon(
            imageVector = getCategoryIcon(iconName),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}
