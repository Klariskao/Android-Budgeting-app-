package com.example.mybudget.data.model

data class Budget(
    val monthlyIncome: Double,
    val yearlyIncome: Double,
    val expenses: List<Expense>
)
