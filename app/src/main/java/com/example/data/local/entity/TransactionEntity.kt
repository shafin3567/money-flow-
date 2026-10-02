package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["accountId"]),
        Index(value = ["toAccountId"]),
        Index(value = ["dateMillis"]),
        Index(value = ["categoryId"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // EXPENSE, INCOME, TRANSFER
    val amountMinorUnits: Long,
    val currencyCode: String,
    val categoryId: Long? = null,
    val accountId: Long,
    val toAccountId: Long? = null,
    val dateMillis: Long,
    val note: String = "",
    val isRecurring: Boolean = false,
    val recurringRuleId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
