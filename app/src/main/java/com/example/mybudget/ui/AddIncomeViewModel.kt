package com.example.mybudget.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Income
import com.example.mybudget.repository.BudgetRepository
import com.example.mybudget.ui.model.AddIncomeEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddIncomeViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    val budget: StateFlow<Budget> = repository.budgetData

    private val _uiEvent = MutableSharedFlow<AddIncomeEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: AddIncomeEvent) {
        when (event) {
            is AddIncomeEvent.AddIncome -> {
                val name = event.name.trim()
                val amount = event.amount.toDoubleOrNull()
                if (name.isEmpty() || amount == null || amount <= 0.0) {
                    viewModelScope.launch {
                        _uiEvent.emit(AddIncomeEvent.ShowToast("Enter valid income info"))
                    }
                } else {
                    repository.addIncome(Income(name, amount, event.type))
                    viewModelScope.launch {
                        _uiEvent.emit(AddIncomeEvent.ShowToast("Income added"))
                    }
                }
            }

            is AddIncomeEvent.ShowToast -> {
                // Handled by the screen
            }
        }
    }
}
