package com.example.mybudget.data.local

import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class MockExpenseDao : ExpenseDao {

    private val expenseList = mutableListOf(
        Expense(
            name = "Netflix Subscription",
            amount = 15.99,
            priority = ExpensePriority.GOOD_TO_HAVE,
            frequency = ExpenseFrequency.MONTHLY,
            category = ExpenseCategory.ENTERTAINMENT,
            customFrequencyInDays = null,
            purchaseDate = LocalDate.of(2025, 5, 1),
            brand = "Netflix",
            provider = "Netflix Inc.",
            linkToPurchase = "https://www.netflix.com",
            nextPurchaseDate = LocalDate.of(2025, 6, 1),
            note = "Monthly subscription for streaming service",
        ),
        Expense(
            name = "Vacation",
            amount = 1234.5,
            priority = ExpensePriority.LUXURY,
            frequency = ExpenseFrequency.YEARLY,
            category = ExpenseCategory.ENTERTAINMENT,
        ),
        Expense(
            name = "Rent",
            amount = 1500.5,
            priority = ExpensePriority.REQUIRED,
            frequency = ExpenseFrequency.MONTHLY,
            category = ExpenseCategory.HOME,
        ),
    )

    override suspend fun insertExpense(expense: Expense): Long {
        expenseList.add(expense)
        return expense.id
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseList.remove(expense)
    }

    override suspend fun getAllExpenses(): List<Expense> = expenseList
    override fun getExpenseById(id: Long): Flow<Expense> = flowOf(expenseList.first())
    override suspend fun updateExpense(expense: Expense) {}
}
