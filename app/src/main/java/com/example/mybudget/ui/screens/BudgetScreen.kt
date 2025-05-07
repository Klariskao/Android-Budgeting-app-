package com.example.mybudget.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mybudget.data.model.Expense
import com.example.mybudget.ui.BudgetViewModel
import com.example.mybudget.ui.navigation.Screen

@Composable
fun BudgetScreen(
    budgetViewModel: BudgetViewModel,
    navController: NavController
) {
    val budget by budgetViewModel.budget.observeAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        // Display Monthly Income
        Text("Monthly Income: ${budget?.monthlyIncome}")

        // Display Available Funds
        val availableFunds = budgetViewModel.calculateAvailableFunds()
        Text("Available Funds: $availableFunds")

        // Display Expenses
        LazyColumn {
            items(budget?.expenses.orEmpty()) { expense ->
                ExpenseItem(expense = expense)
            }
        }

        // Add Expense Button
        Button(
            onClick = {
                navController.navigate(Screen.Summary.route)
            }
        ) {
            Text("Add Expense")
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Text(text = "${expense.name} - ${expense.amount} (${expense.type})")
}
