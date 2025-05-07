package com.example.mybudget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybudget.data.model.Expense
import com.example.mybudget.repository.BudgetRepository
import com.example.mybudget.ui.model.AddExpenseEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddExpenseViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<AddExpenseEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: AddExpenseEvent) {
        when (event) {
            is AddExpenseEvent.AddExpense -> if (event.name.isNotBlank() && event.amount.toDoubleOrNull() != null) {
                val expense = Expense(
                    name = event.name,
                    amount = event.amount.toDouble(),
                    type = event.type,
                    frequency = event.frequency
                )
                repository.addExpense(expense)
            } else {
                viewModelScope.launch {
                    _uiEvent.emit(AddExpenseEvent.ShowToast("Please enter valid name and amount."))
                }
            }

            is AddExpenseEvent.ShowToast -> {
                // Handled by the screen
            }
        }
    }
}
