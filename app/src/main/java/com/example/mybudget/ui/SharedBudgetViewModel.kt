package com.example.mybudget.ui

import androidx.lifecycle.ViewModel
import com.example.mybudget.data.model.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedBudgetViewModel : ViewModel() {
    private val _budget = MutableStateFlow(Budget())
    val budget: StateFlow<Budget> = _budget.asStateFlow()

    fun setBudget(newBudget: Budget) {
        _budget.value = newBudget
    }
}
