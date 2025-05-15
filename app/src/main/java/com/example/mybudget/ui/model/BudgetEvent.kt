package com.example.mybudget.ui.model

import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.Income

sealed class BudgetEvent {
    data class EditIncome(val income: Income) : BudgetEvent()
    data class EditExpense(val expense: Expense) : BudgetEvent()
    data class ConfirmRemoveIncome(val income: Income) : BudgetEvent()
    data class ConfirmRemoveExpense(val expense: Expense) : BudgetEvent()
    data class AddIncome(val income: Income) : BudgetEvent()
    data class AddExpense(val expense: Expense) : BudgetEvent()
    data class UpdateIncome(val income: Income) : BudgetEvent()
    data class UpdateExpense(val expense: Expense) : BudgetEvent()
    data class RemoveIncome(val income: Income) : BudgetEvent()
    data class RemoveExpense(val expense: Expense) : BudgetEvent()
    data object CloseDialog : BudgetEvent()
}
