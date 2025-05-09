package com.example.mybudget.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.Income
import com.example.mybudget.data.model.IncomeType
import com.example.mybudget.repository.BudgetRepository

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val _budget = MutableLiveData(Budget())
    val budget: LiveData<Budget> get() = _budget

    fun updateIncome(income: Income) {
        _budget.value = _budget.value?.copy(incomes = _budget.value?.incomes.orEmpty() + income)
    }

    fun addExpense(expense: Expense) {
        _budget.value = _budget.value?.copy(expenses = _budget.value?.expenses.orEmpty() + expense)
    }

    fun removeExpense(expense: Expense) {
        _budget.value = _budget.value?.copy(expenses = _budget.value?.expenses.orEmpty() - expense)
    }

    fun calculateAvailableFunds(): Double {
        val totalMonthlyExpenses =
            _budget.value?.expenses?.filter {
                it.frequency == ExpenseFrequency.MONTHLY
            }?.sumOf { it.amount } ?: 0.0
        val totalMonthlyIncome =
            _budget.value?.incomes?.filter {
                it.type == IncomeType.MONTHLY
            }?.sumOf { it.amount } ?: 0.0
        return totalMonthlyIncome - totalMonthlyExpenses
    }
}
