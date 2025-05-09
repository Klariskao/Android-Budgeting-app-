package com.example.mybudget.ui.model

import com.example.mybudget.data.model.IncomeType

sealed class AddIncomeEvent {
    data class AddIncome(
        val name: String,
        val amount: String,
        val type: IncomeType
    ) : AddIncomeEvent()

    data object IncomeAdded : AddIncomeEvent()

    data class ShowToast(val message: String) : AddIncomeEvent()
}