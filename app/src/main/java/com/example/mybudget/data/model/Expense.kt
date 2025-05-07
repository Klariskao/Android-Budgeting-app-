package com.example.mybudget.data.model

data class Expense(
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