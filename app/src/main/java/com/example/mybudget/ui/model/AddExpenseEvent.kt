package com.example.mybudget.ui.model

import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import java.time.LocalDate

sealed class AddExpenseEvent {
    data class AddExpense(
        val name: String,
        val amount: String,
        val type: ExpensePriority,
        val frequency: ExpenseFrequency,
        val category: ExpenseCategory,
        val customFrequencyInDays: Int? = null,
        val purchaseDate: LocalDate,
    ) : AddExpenseEvent()

    data object ExpenseAdded : AddExpenseEvent()

    data class ShowToast(val message: String) : AddExpenseEvent()
}
