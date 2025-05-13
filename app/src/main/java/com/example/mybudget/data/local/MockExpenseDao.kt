package com.example.mybudget.data.local

import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority

class MockExpenseDao : ExpenseDao {

    private val expenseList = mutableListOf(
        Expense(
            name = "Vacation",
            amount = 1234.5,
            priority = ExpensePriority.LUXURY,
            frequency = ExpenseFrequency.YEARLY,
            category = ExpenseCategory.ENTERTAINMENT
        )
    )

    override suspend fun insertExpense(expense: Expense): Long {
        expenseList.add(expense)
        return expense.id
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseList.remove(expense)
    }

    override suspend fun getAllExpenses(): List<Expense> {
        return expenseList
    }
}
