package com.example.mybudget.data.local

import com.example.mybudget.data.model.Expense

class MockExpenseDao : ExpenseDao {

    private val expenseList = mutableListOf<Expense>()

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
