package com.example.mybudget.ui.model

import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import java.time.LocalDate

sealed class AddExpenseEvent {
    data class AddExpense(
        val name: String,
        val amount: String,
        val priority: ExpensePriority,
        val frequency: ExpenseFrequency,
        val category: ExpenseCategory,
        val customFrequencyInDays: Int?,
        val purchaseDate: LocalDate,
        val brand: String,
        val provider: String,
        val linkToPurchase: String,
        val note: String,
        val repetitions: Int?,
        val endDate: LocalDate?,
    ) : AddExpenseEvent()

    data object ExpenseAdded : AddExpenseEvent()

    data class ShowToast(val message: String) : AddExpenseEvent()
}
