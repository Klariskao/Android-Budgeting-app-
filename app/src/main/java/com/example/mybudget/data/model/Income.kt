package com.example.mybudget.data.model

data class Income(
    val name: String,
    val amount: Double,
    val type: IncomeType
)

enum class IncomeType {
    WEEKLY,
    BI_WEEKLY,
    MONTHLY,
    YEARLY,
    ONE_TIME
}
