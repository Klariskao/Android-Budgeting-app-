package com.example.mybudget.ui.model

import com.example.mybudget.data.model.IncomeFrequency
import java.time.LocalDate

sealed class AddIncomeEvent {
    data class AddIncome(
        val name: String,
        val amount: String,
        val frequency: IncomeFrequency,
        val firstPaymentDate: LocalDate,
        val customFrequencyInDays: Int? = null,
    ) : AddIncomeEvent()

    data object IncomeAdded : AddIncomeEvent()

    data class ShowToast(val message: String) : AddIncomeEvent()
}
