package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    indices = [
        Index(value = ["categoryId"])
    ]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val limitMinorUnits: Long,
    val currencyCode: String,
    val categoryId: Long? = null,
    val monthYear: String = "ALL", // "ALL" or "YYYY-MM"
    val notify75: Boolean = true,
    val notify90: Boolean = true,
    val notify100: Boolean = true
)
