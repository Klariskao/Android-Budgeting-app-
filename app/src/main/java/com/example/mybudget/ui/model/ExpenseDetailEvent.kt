package com.example.mybudget.ui.model

import com.example.mybudget.data.model.Expense

sealed class ExpenseDetailEvent {
    data class UpdateExpense(val expense: Expense) : ExpenseDetailEvent()
    data class RemoveExpense(val expense: Expense) : ExpenseDetailEvent()
}
