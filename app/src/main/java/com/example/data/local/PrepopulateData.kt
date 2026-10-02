package com.example.data.local

import com.example.data.local.entity.CategoryEntity

object PrepopulateData {
    val DEFAULT_CATEGORIES = listOf(
        // Expense categories
        CategoryEntity(id = 1, name = "Food & Dining", type = "EXPENSE", iconName = "restaurant", colorHex = "#F7C59F", isDefault = true),
        CategoryEntity(id = 2, name = "Groceries", type = "EXPENSE", iconName = "shopping_cart", colorHex = "#72B095", isDefault = true),
        CategoryEntity(id = 3, name = "Shopping", type = "EXPENSE", iconName = "shopping_bag", colorHex = "#F8B195", isDefault = true),
        CategoryEntity(id = 4, name = "Transport", type = "EXPENSE", iconName = "directions_car", colorHex = "#6CA8E8", isDefault = true),
        CategoryEntity(id = 5, name = "Housing & Rent", type = "EXPENSE", iconName = "home", colorHex = "#9C8EB9", isDefault = true),
        CategoryEntity(id = 6, name = "Utilities & Bills", type = "EXPENSE", iconName = "receipt_long", colorHex = "#E7A838", isDefault = true),
        CategoryEntity(id = 7, name = "Entertainment", type = "EXPENSE", iconName = "movie", colorHex = "#E4A4BF", isDefault = true),
        CategoryEntity(id = 8, name = "Health & Medical", type = "EXPENSE", iconName = "medical_services", colorHex = "#52B788", isDefault = true),
        CategoryEntity(id = 9, name = "Personal Care", type = "EXPENSE", iconName = "spa", colorHex = "#FFB4A2", isDefault = true),
        CategoryEntity(id = 10, name = "Education", type = "EXPENSE", iconName = "school", colorHex = "#8ECAE6", isDefault = true),
        CategoryEntity(id = 11, name = "Travel", type = "EXPENSE", iconName = "flight", colorHex = "#7CB9E8", isDefault = true),
        CategoryEntity(id = 12, name = "Other Expense", type = "EXPENSE", iconName = "more_horiz", colorHex = "#B0B7C3", isDefault = true),

        // Income categories
        CategoryEntity(id = 13, name = "Salary", type = "INCOME", iconName = "payments", colorHex = "#52B788", isDefault = true),
        CategoryEntity(id = 14, name = "Freelance & Business", type = "INCOME", iconName = "work", colorHex = "#6CA8E8", isDefault = true),
        CategoryEntity(id = 15, name = "Investments & Dividends", type = "INCOME", iconName = "trending_up", colorHex = "#9C8EB9", isDefault = true),
        CategoryEntity(id = 16, name = "Gifts", type = "INCOME", iconName = "card_giftcard", colorHex = "#F7C59F", isDefault = true),
        CategoryEntity(id = 17, name = "Other Income", type = "INCOME", iconName = "attach_money", colorHex = "#72B095", isDefault = true)
    )
}
