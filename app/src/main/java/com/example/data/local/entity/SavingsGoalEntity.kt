package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "savings_goals",
    indices = [
        Index(value = ["targetDateMillis"])
    ]
)
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetMinorUnits: Long,
    val currentMinorUnits: Long = 0L,
    val currencyCode: String,
    val targetDateMillis: Long,
    val iconName: String = "flag",
    val colorHex: String = "#F7C59F",
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
