package com.example.mybudget.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.IncomeType
import com.example.mybudget.repository.BudgetRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    val budget: StateFlow<Budget> = repository.budgetData

    init {
        viewModelScope.launch {
            repository.loadBudgetFromDatabase()
        }
    }

    fun calculateAvailableFunds(): Double {
        val currentBudget = budget.value
        val totalMonthlyIncome = currentBudget.incomes
            .filter { it.type == IncomeType.MONTHLY }
            .sumOf { it.amount }

        val totalMonthlyExpenses = currentBudget.expenses
            .filter { it.frequency == ExpenseFrequency.MONTHLY }
            .sumOf { it.amount }

        return totalMonthlyIncome - totalMonthlyExpenses
    }
}
