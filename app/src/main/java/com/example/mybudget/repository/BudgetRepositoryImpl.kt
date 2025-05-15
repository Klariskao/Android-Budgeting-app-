package com.example.mybudget.repository

import com.example.mybudget.data.local.ExpenseDao
import com.example.mybudget.data.local.IncomeDao
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.Income
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BudgetRepositoryImpl(private val expenseDao: ExpenseDao, private val incomeDao: IncomeDao) :
    BudgetRepository {

    // In-memory storage for budget data
    private val _budgetData = MutableStateFlow(
        Budget(
            incomes = emptyList(),
            expenses = emptyList(),
        ),
    )
    override val budgetData: StateFlow<Budget> = _budgetData.asStateFlow()

    override suspend fun getBudget(): Budget = _budgetData.value

    override suspend fun saveBudget(budget: Budget) {
        _budgetData.value = budget
    }

    override suspend fun addIncome(income: Income) {
        val id = incomeDao.insertIncome(income)
        val savedIncome = income.copy(id = id)
        _budgetData.value = _budgetData.value.copy(
            incomes = _budgetData.value.incomes + savedIncome,
        )
    }

    override suspend fun removeIncome(income: Income) {
        incomeDao.deleteIncome(income)
        _budgetData.value = _budgetData.value.copy(
            incomes = _budgetData.value.incomes - income,
        )
    }

    override suspend fun addExpense(expense: Expense) {
        val id = expenseDao.insertExpense(expense)
        val savedExpense = expense.copy(id = id)
        _budgetData.value = _budgetData.value.copy(
            expenses = _budgetData.value.expenses + savedExpense,
        )
    }

    override suspend fun removeExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
        _budgetData.value = _budgetData.value.copy(
            expenses = _budgetData.value.expenses - expense,
        )
    }

    override suspend fun loadBudgetFromDatabase() {
        val expenses = expenseDao.getAllExpenses()
        val incomes = incomeDao.getAllIncomes()
        _budgetData.value = Budget(incomes = incomes, expenses = expenses)
    }

    override suspend fun saveBudgetToDatabase(budget: Budget) {
        // Save all expenses from the budget
        // For simplicity, delete all and re-insert (you could optimize this)
        val existing = expenseDao.getAllExpenses()
        existing.forEach { expenseDao.deleteExpense(it) }

        budget.expenses.forEach { expenseDao.insertExpense(it) }
    }
}
