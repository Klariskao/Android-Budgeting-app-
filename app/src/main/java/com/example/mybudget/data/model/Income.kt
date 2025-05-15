package com.example.mybudget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "incomes")
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val frequency: IncomeFrequency,
    val firstPaymentDate: LocalDate = LocalDate.now(),
    val customFrequencyInDays: Int? = null,
)

enum class IncomeFrequency {
    WEEKLY,
    BI_WEEKLY,
    MONTHLY,
    YEARLY,
    ONE_TIME,
    CUSTOM,
}
