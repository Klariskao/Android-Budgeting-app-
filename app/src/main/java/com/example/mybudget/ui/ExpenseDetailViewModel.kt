package com.example.mybudget.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import com.example.mybudget.repository.BudgetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ExpenseDetailViewModel(savedStateHandle: SavedStateHandle, repository: BudgetRepository) :
    ViewModel() {

    private val expenseId: Long = savedStateHandle.get<Long>("expenseId")
        ?: throw IllegalArgumentException("Missing expenseId")

    val expense: StateFlow<Expense> =
        repository
            .getExpenseById(expenseId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Expense(
                    name = "",
                    amount = 0.0,
                    priority = ExpensePriority.REQUIRED,
                    frequency = ExpenseFrequency.YEARLY,
                    category = ExpenseCategory.HOME,
                ),
            )

    init {
        Log.d("ExpenseDetailVM", "expenseId = $expenseId")
    }
}
