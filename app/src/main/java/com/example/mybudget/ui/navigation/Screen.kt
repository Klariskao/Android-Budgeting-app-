package com.example.mybudget.ui.navigation

sealed class Screen(val route: String) {
    data object Budget : Screen("budget")
    data object Expense : Screen("expense")
    data object Income : Screen("income")
    data object ExpenseDetail : Screen("expense_detail/{expenseId}") {
        fun createRoute(expenseId: Long) = "expense_detail/$expenseId"
    }

    data object Settings : Screen("settings")
}
