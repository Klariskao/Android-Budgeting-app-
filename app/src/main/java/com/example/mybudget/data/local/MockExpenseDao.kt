package com.example.mybudget.data.local

import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpenseType

class MockExpenseDao : ExpenseDao {

    private val expenseList = mutableListOf(
        Expense(
            name = "Vacation",
            amount = 1234.5,
            type = ExpenseType.LUXURY,
            frequency = ExpenseFrequency.ONE_TIME
        )
    )

    override suspend fun insertExpense(expense: Expense) {
        expenseList.add(expense)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseList.remove(expense)
    }

    override suspend fun getAllExpenses(): List<Expense> {
        return expenseList
    }
}
