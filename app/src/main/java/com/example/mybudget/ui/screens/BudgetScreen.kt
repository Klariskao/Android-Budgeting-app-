package com.example.mybudget.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybudget.data.local.MockExpenseDao
import com.example.mybudget.data.model.Expense
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.BudgetViewModel
import com.example.mybudget.ui.navigation.Screen

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
    navController: NavController
) {
    val budget by viewModel.budget.observeAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        // Display Monthly Income
        Text("Monthly Income: ${budget?.monthlyIncome}")

        // Display Available Funds
        val availableFunds = viewModel.calculateAvailableFunds()
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

@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    MaterialTheme {
        BudgetScreen(
            viewModel = BudgetViewModel(BudgetRepositoryImpl(MockExpenseDao())),
            navController = rememberNavController()
        )
    }
}
