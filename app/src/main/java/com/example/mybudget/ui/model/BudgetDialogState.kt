package com.example.mybudget.ui.model

import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.Income

sealed class BudgetDialogState {
    data class ConfirmDeleteIncome(val income: Income) : BudgetDialogState()
    data class ConfirmDeleteExpense(val expense: Expense) : BudgetDialogState()
    data class EditIncome(val income: Income) : BudgetDialogState()
    data class EditExpense(val expense: Expense) : BudgetDialogState()
}
