package com.example.mybudget.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Expense
import com.example.mybudget.repository.BudgetRepository
import com.example.mybudget.ui.model.AddExpenseEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddExpenseViewModel(private val repository: BudgetRepository) : ViewModel() {

    val budget: StateFlow<Budget> = repository.budgetData

    private val _uiEvent = MutableSharedFlow<AddExpenseEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.loadBudgetFromDatabase()
        }
    }

    fun onEvent(event: AddExpenseEvent) {
        when (event) {
            is AddExpenseEvent.AddExpense -> if (event.name.isNotBlank() &&
                event.amount.toDoubleOrNull() != null
            ) {
                val expense = Expense(
                    name = event.name,
                    amount = event.amount.toDouble(),
                    priority = event.priority,
                    frequency = event.frequency,
                    category = event.category,
                    customFrequencyInDays = event.customFrequencyInDays,
                    purchaseDate = event.purchaseDate,
                    brand = event.brand,
                    provider = event.provider,
                    linkToPurchase = event.linkToPurchase,
                    note = event.note,
                    repetitions = event.repetitions,
                    endDate = event.endDate,
                )

                viewModelScope.launch {
                    repository.addExpense(expense)
                    _uiEvent.emit(AddExpenseEvent.ShowToast("Expense added"))
                    _uiEvent.emit(AddExpenseEvent.ExpenseAdded)
                }
            } else {
                viewModelScope.launch {
                    _uiEvent.emit(AddExpenseEvent.ShowToast("Please enter valid name and amount."))
                }
            }

            is AddExpenseEvent.ShowToast, AddExpenseEvent.ExpenseAdded -> {
                // Handled by the screen
            }
        }
    }
}
