package com.example.mybudget.repository

import com.example.mybudget.data.local.ExpenseDao
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.Income
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BudgetRepositoryImpl(
    private val expenseDao: ExpenseDao
) : BudgetRepository {

    // In-memory storage for budget data
    private var budgetData: Budget = Budget(
        incomes = emptyList(),
        expenses = emptyList()
    )

    override fun getBudget(): Budget {
        return budgetData
    }

    override fun saveBudget(budget: Budget) {
        budgetData = budget
        // Save to DB
        saveBudgetToDatabase(budget)
    }

    override fun addExpense(expense: Expense) {
        budgetData = budgetData.copy(expenses = budgetData.expenses + expense)
        // Save to DB
        CoroutineScope(Dispatchers.IO).launch {
            expenseDao.insertExpense(expense)
        }
    }

    override fun removeExpense(expense: Expense) {
        budgetData = budgetData.copy(expenses = budgetData.expenses - expense)
        // Delete from DB
        CoroutineScope(Dispatchers.IO).launch {
            expenseDao.deleteExpense(expense)
        }
    }

    override fun loadBudgetFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val expenses = expenseDao.getAllExpenses()
            budgetData = budgetData.copy(expenses = expenses)
        }
    }

    override fun saveBudgetToDatabase(budget: Budget) {
        CoroutineScope(Dispatchers.IO).launch {
            // Save all expenses from the budget
            // For simplicity, delete all and re-insert (you could optimize this)
            val existing = expenseDao.getAllExpenses()
            existing.forEach { expenseDao.deleteExpense(it) }

            budget.expenses.forEach { expenseDao.insertExpense(it) }
        }
    }

    override fun addIncome(income: Income) {
        budgetData = budgetData.copy(incomes = budgetData.incomes + income)
    }

    override fun removeIncome(income: Income) {
        budgetData = budgetData.copy(incomes = budgetData.incomes - income)
    }

}
