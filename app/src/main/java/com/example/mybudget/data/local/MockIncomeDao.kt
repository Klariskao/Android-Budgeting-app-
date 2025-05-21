package com.example.mybudget.data.local

import com.example.mybudget.data.model.Income
import com.example.mybudget.data.model.IncomeFrequency

class MockIncomeDao : IncomeDao {

    private val incomeList = mutableListOf(
        Income(
            name = "Job",
            amount = 1500.0,
            frequency = IncomeFrequency.MONTHLY,
        ),
    )

    override suspend fun insertIncome(income: Income): Long {
        incomeList.add(income)
        return income.id
    }

    override suspend fun deleteIncome(income: Income) {
        incomeList.remove(income)
    }

    override suspend fun getAllIncomes(): List<Income> = incomeList.toList()
    override suspend fun insertAll(incomes: List<Income>) {}
    override suspend fun clearAll() {}
}
