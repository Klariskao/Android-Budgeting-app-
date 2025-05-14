package com.example.mybudget.ui.model

enum class ExpensesSortOption(val label: String) {
    DATE_DESC("Date ↓"),
    DATE_ASC("Date ↑"),
    AMOUNT_DESC("Amount ↓"),
    AMOUNT_ASC("Amount ↑"),
    FREQUENCY("Frequency"),
    NAME("Name"),
    NONE("None")
}
