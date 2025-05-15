package com.example.mybudget.data.model

data class Budget(
    val incomes: List<Income> = emptyList(),
    val expenses: List<Expense> = emptyList(),
)
