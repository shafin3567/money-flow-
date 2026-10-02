package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // CASH, BANK, CREDIT_CARD, SAVINGS, WALLET, OTHER
    val currencyCode: String,
    val initialBalanceMinorUnits: Long = 0L,
    val colorHex: String = "#7CB9E8",
    val iconName: String = "account_balance",
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
