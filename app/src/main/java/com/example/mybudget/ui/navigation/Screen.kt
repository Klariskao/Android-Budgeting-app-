package com.example.mybudget.ui.navigation

sealed class Screen(val route: String) {
    data object Budget : Screen("budget")
    data object Expense : Screen("expense")
    data object Income : Screen("income")
}
