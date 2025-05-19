package com.example.mybudget.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybudget.data.model.Expense
import com.example.mybudget.repository.BudgetRepository
import com.example.mybudget.ui.model.ExpenseDetailEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ExpenseDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: BudgetRepository,
) : ViewModel() {

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    private val expenseId: Long = savedStateHandle.get<Long>("expenseId")
        ?: throw IllegalArgumentException("Missing expenseId")

    val expenseFlow: Flow<Expense> = repository.getExpenseById(expenseId)

    init {
        Log.d("ExpenseDetailVM", "expenseId = $expenseId")
    }

    fun onEvent(event: ExpenseDetailEvent) {
        Log.d("ExpenseDetailVM", "Event triggered: $event")
        when (event) {
            is ExpenseDetailEvent.UpdateExpense -> updateExpense(event.expense)
            is ExpenseDetailEvent.RemoveExpense -> deleteExpense(event.expense)
        }
    }

    private fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    private fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.removeExpense(expense)
            _navigateBack.emit(Unit)
        }
    }
}
