package com.example.mybudget.repository

import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Expense

interface BudgetRepository {
    fun getBudget(): Budget
    fun saveBudget(budget: Budget)
    fun addExpense(expense: Expense)
    fun removeExpense(expense: Expense)
    fun loadBudgetFromDatabase()
    fun saveBudgetToDatabase(budget: Budget)
}