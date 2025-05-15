package com.example.mybudget.repository

import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.Income
import kotlinx.coroutines.flow.StateFlow

interface BudgetRepository {
    val budgetData: StateFlow<Budget>

    suspend fun getBudget(): Budget
    suspend fun saveBudget(budget: Budget)
    suspend fun addExpense(expense: Expense)
    suspend fun removeExpense(expense: Expense)
    suspend fun loadBudgetFromDatabase()
    suspend fun saveBudgetToDatabase(budget: Budget)
    suspend fun addIncome(income: Income)
    suspend fun removeIncome(income: Income)
}
