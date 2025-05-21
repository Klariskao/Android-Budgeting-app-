package com.example.mybudget.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val incomes: List<Income> = emptyList(),
    val expenses: List<Expense> = emptyList(),
)
