package com.example.mybudget.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.Income
import com.example.mybudget.data.model.IncomeFrequency
import com.example.mybudget.repository.BudgetRepository
import com.example.mybudget.ui.model.BudgetDialogState
import com.example.mybudget.ui.model.BudgetEvent
import com.example.mybudget.ui.model.ExpensesSortOption
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    val budget: StateFlow<Budget> = repository.budgetData

    var dialogState by mutableStateOf<BudgetDialogState?>(null)

    init {
        viewModelScope.launch {
            repository.loadBudgetFromDatabase()
        }
    }

    fun onEvent(event: BudgetEvent) {
        Log.d("BudgetEvent", "Event triggered: $event")
        when (event) {
            is BudgetEvent.EditIncome -> dialogState = BudgetDialogState.EditIncome(event.income)
            is BudgetEvent.EditExpense -> dialogState = BudgetDialogState.EditExpense(event.expense)
            is BudgetEvent.ConfirmRemoveIncome ->
                dialogState = BudgetDialogState.ConfirmDeleteIncome(event.income)

            is BudgetEvent.ConfirmRemoveExpense ->
                dialogState = BudgetDialogState.ConfirmDeleteExpense(event.expense)

            is BudgetEvent.AddIncome -> viewModelScope.launch { repository.addIncome(event.income) }
            is BudgetEvent.AddExpense -> viewModelScope.launch {
                repository.addExpense(event.expense)
            }

            is BudgetEvent.RemoveIncome -> viewModelScope.launch {
                repository.removeIncome(event.income)
            }

            is BudgetEvent.RemoveExpense -> viewModelScope.launch {
                repository.removeExpense(event.expense)
            }

            is BudgetEvent.UpdateIncome -> updateIncome(event.income)
            is BudgetEvent.UpdateExpense -> updateExpense(event.expense)
            BudgetEvent.CloseDialog -> dialogState = null
        }
    }

    fun calculateAvailableFunds(): Double {
        val currentBudget = budget.value
        val totalMonthlyIncome = currentBudget.incomes
            .filter { it.frequency == IncomeFrequency.MONTHLY }
            .sumOf { it.amount }

        val totalMonthlyExpenses = currentBudget.expenses
            .filter { it.frequency == ExpenseFrequency.MONTHLY }
            .sumOf { it.amount }

        return totalMonthlyIncome - totalMonthlyExpenses
    }

    fun sortExpenses(expenses: List<Expense>, sortOption: ExpensesSortOption): List<Expense> =
        when (sortOption) {
            ExpensesSortOption.DATE_DESC -> expenses.sortedByDescending { it.purchaseDate }
            ExpensesSortOption.DATE_ASC -> expenses.sortedBy { it.purchaseDate }
            ExpensesSortOption.AMOUNT_DESC -> expenses.sortedByDescending { it.amount }
            ExpensesSortOption.AMOUNT_ASC -> expenses.sortedBy { it.amount }
            ExpensesSortOption.FREQUENCY -> expenses.sortedBy { it.frequency.name }
            ExpensesSortOption.NAME -> expenses.sortedBy { it.name }
            ExpensesSortOption.NONE -> expenses
        }

    private fun updateIncome(edited: Income) {
        viewModelScope.launch {
            val current = budget.value.incomes.toMutableList()
            val index = current.indexOfFirst { it.id == edited.id }
            if (index != -1) {
                current[index] = edited
                repository.saveBudget(budget.value.copy(incomes = current))
            }
        }
    }

    private fun updateExpense(edited: Expense) {
        viewModelScope.launch {
            val current = budget.value.expenses.toMutableList()
            val index = current.indexOfFirst { it.id == edited.id }
            if (index != -1) {
                current[index] = edited
                repository.saveBudget(budget.value.copy(expenses = current))
            }
        }
    }
}
