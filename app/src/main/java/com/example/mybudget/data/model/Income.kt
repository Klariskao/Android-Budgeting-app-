package com.example.mybudget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incomes")
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val type: IncomeType
)

enum class IncomeType {
    WEEKLY,
    BI_WEEKLY,
    MONTHLY,
    YEARLY,
    ONE_TIME
}
