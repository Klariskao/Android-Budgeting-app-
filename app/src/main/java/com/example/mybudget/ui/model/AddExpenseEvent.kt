package com.example.mybudget.ui.model

import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpenseType

sealed class AddExpenseEvent {
    data class AddExpense(
        val name: String,
        val amount: String,
        val type: ExpenseType,
        val frequency: ExpenseFrequency
    ) : AddExpenseEvent()

    data class ShowToast(val message: String) : AddExpenseEvent()
}