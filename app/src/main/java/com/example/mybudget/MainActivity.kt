package com.example.mybudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mybudget.ui.AddExpenseViewModel
import com.example.mybudget.ui.AddIncomeViewModel
import com.example.mybudget.ui.BudgetViewModel
import com.example.mybudget.ui.ExpenseDetailViewModel
import com.example.mybudget.ui.ThemeViewModel
import com.example.mybudget.ui.navigation.Screen
import com.example.mybudget.ui.screens.AddExpenseScreen
import com.example.mybudget.ui.screens.AddIncomeScreen
import com.example.mybudget.ui.screens.BudgetScreen
import com.example.mybudget.ui.screens.ExpenseDetailScreen
import com.example.mybudget.ui.screens.SettingsScreen
import com.example.mybudget.ui.theme.MyBudgetTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val budgetViewModel: BudgetViewModel by viewModel()
    private val addExpenseViewModel: AddExpenseViewModel by viewModel()
    private val addIncomeViewModel: AddIncomeViewModel by viewModel()
    private val themeViewModel: ThemeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            MyBudgetTheme(isDarkTheme = isDarkTheme) {
                val navController = rememberNavController()
                NavHost(
                    navController,
                    startDestination = Screen.Budget.route,
                ) {
                    composable(Screen.Budget.route) {
                        BudgetScreen(viewModel = budgetViewModel, navController)
                    }
                    composable(Screen.Expense.route) {
                        AddExpenseScreen(viewModel = addExpenseViewModel, navController)
                    }
                    composable(Screen.Income.route) {
                        AddIncomeScreen(viewModel = addIncomeViewModel, navController)
                    }
                    composable(
                        route = Screen.ExpenseDetail.route,
                        arguments = listOf(
                            navArgument("expenseId") {
                                type = NavType.LongType
                            },
                        ),
                    ) { backStackEntry ->
                        val viewModel: ExpenseDetailViewModel = koinViewModel(
                            viewModelStoreOwner = backStackEntry,
                        )
                        ExpenseDetailScreen(viewModel = viewModel, navController)
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen()
                    }
                }
            }
        }
    }
}
