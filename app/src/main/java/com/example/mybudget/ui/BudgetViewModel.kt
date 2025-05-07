package com.example.mybudget.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.repository.BudgetRepository

class BudgetViewModel(private val budgetRepository: BudgetRepository) : ViewModel() {

    private val _budget = MutableLiveData<Budget>()
    val budget: LiveData<Budget> get() = _budget

    // Initialize with an empty budget or default values
    init {
        _budget.value = Budget(monthlyIncome = 0.0, yearlyIncome = 0.0, expenses = emptyList())
    }

    fun updateMonthlyIncome(amount: Double) {
        _budget.value = _budget.value?.copy(monthlyIncome = amount)
    }

    fun updateYearlyIncome(amount: Double) {
        _budget.value = _budget.value?.copy(yearlyIncome = amount)
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
        return (budget.value?.monthlyIncome ?: 0.0) - totalMonthlyExpenses
    }
}
