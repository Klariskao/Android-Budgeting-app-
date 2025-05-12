package com.example.mybudget.data.local

import com.example.mybudget.data.model.Income
import com.example.mybudget.data.model.IncomeType

class MockIncomeDao : IncomeDao {

    private val incomeList = mutableListOf(
        Income(
            name = "Job",
            amount = 1500.0,
            type = IncomeType.MONTHLY
        )
    )

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
