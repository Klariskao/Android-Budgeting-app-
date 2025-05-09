package com.example.mybudget.data.local

import com.example.mybudget.data.model.Income

class MockIncomeDao : IncomeDao {

    private val incomeList = mutableListOf<Income>()

    override suspend fun insertIncome(income: Income) {
        incomeList.add(income)
    }

    override suspend fun deleteIncome(income: Income) {
        incomeList.remove(income)
    }

    override suspend fun getAllIncomes(): List<Income> {
        return incomeList.toList()
    }
}
