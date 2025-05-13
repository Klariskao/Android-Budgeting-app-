package com.example.mybudget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val priority: ExpensePriority,
    val frequency: ExpenseFrequency,
    val category: ExpenseCategory,
    val customFrequencyInDays: Int? = null, // Null unless frequency is CUSTOM
    val purchaseDate: LocalDate = LocalDate.now()
)


enum class ExpensePriority {
    REQUIRED, GOOD_TO_HAVE, NICE_TO_HAVE, LUXURY
}

enum class ExpenseFrequency {
    DAILY, WEEKLY, BIWEEKLY, MONTHLY, YEARLY, CUSTOM
}

enum class ExpenseCategory {
    HOME, FOOD, HEALTH, TRANSPORT, ENTERTAINMENT, OTHER
}
