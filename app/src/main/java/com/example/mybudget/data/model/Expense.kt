package com.example.mybudget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val type: ExpenseType,
    val frequency: ExpenseFrequency
)

enum class ExpenseType {
    REQUIRED, LUXURY
}

enum class ExpenseFrequency {
    MONTHLY, YEARLY, ONE_TIME
}