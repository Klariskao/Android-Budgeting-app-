package com.example.mybudget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mybudget.data.util.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(tableName = "incomes")
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val frequency: IncomeFrequency,
    @Serializable(with = LocalDateSerializer::class)
    val firstPaymentDate: LocalDate = LocalDate.now(),
    val customFrequencyInDays: Int? = null,
)

@Serializable
enum class IncomeFrequency(val label: String) {
    WEEKLY("Weekly"),
    BI_WEEKLY("Bi-weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly"),
    ONE_TIME("One time"),
    CUSTOM("Custom"),
}
