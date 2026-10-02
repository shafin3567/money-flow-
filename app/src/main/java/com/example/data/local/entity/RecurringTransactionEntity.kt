package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recurring_transactions",
    indices = [
        Index(value = ["accountId"]),
        Index(value = ["categoryId"]),
        Index(value = ["nextDueDateMillis"])
    ]
)
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String, // EXPENSE, INCOME, TRANSFER
    val amountMinorUnits: Long,
    val currencyCode: String,
    val categoryId: Long? = null,
    val accountId: Long,
    val toAccountId: Long? = null,
    val frequency: String, // DAILY, WEEKLY, MONTHLY, YEARLY
    val nextDueDateMillis: Long,
    val startDateMillis: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val note: String = ""
)
