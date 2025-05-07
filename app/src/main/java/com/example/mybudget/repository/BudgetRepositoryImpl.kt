package com.example.mybudget.repository

import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Expense

class BudgetRepositoryImpl : BudgetRepository {

    // In-memory storage for budget data
    private var budgetData: Budget = Budget(
        monthlyIncome = 0.0,
        yearlyIncome = 0.0,
        expenses = emptyList()
    )

    override fun getBudget(): Budget {
        return budgetData
    }

    override fun saveBudget(budget: Budget) {
        budgetData = budget
    }

    override fun addExpense(expense: Expense) {
        budgetData = budgetData.copy(expenses = budgetData.expenses + expense)
    }

    override fun removeExpense(expense: Expense) {
        budgetData = budgetData.copy(expenses = budgetData.expenses - expense)
    }

    // TODO load data from local storage or a remote server
    override fun loadBudgetFromDatabase() {
    }

    // TODO save data to local storage or a remote server
    override fun saveBudgetToDatabase(budget: Budget) {
    }
}
