package com.example.data.model

enum class TransactionType {
    EXPENSE,
    INCOME,
    TRANSFER
}

enum class Frequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

enum class AccountType {
    CASH,
    BANK,
    CREDIT_CARD,
    SAVINGS,
    WALLET,
    OTHER
}

data class InsightItem(
    val title: String,
    val description: String,
    val type: InsightType,
    val iconName: String = "info"
)

enum class InsightType {
    POSITIVE,
    WARNING,
    NEUTRAL
}
