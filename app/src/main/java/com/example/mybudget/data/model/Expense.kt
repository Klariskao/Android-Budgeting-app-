package com.example.mybudget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mybudget.data.util.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val priority: ExpensePriority,
    val frequency: ExpenseFrequency,
    val category: ExpenseCategory,
    val customFrequencyInDays: Int? = null, // Null unless frequency is CUSTOM
    @Serializable(with = LocalDateSerializer::class)
    val purchaseDate: LocalDate = LocalDate.now(),
    val brand: String = "",
    val provider: String = "",
    val linkToPurchase: String = "",
    val note: String? = null,
    val repetitions: Int? = null, // Number of total repetitions, null means infinite
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate? = null, // Optional end date for recurrence
)

@Serializable
enum class ExpensePriority(val label: String) {
    REQUIRED("Required"),
    GOOD_TO_HAVE("Good to have"),
    NICE_TO_HAVE("Nice to have"),
    LUXURY("Luxury"),
}

@Serializable
enum class ExpenseFrequency(val label: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    BI_WEEKLY("Bi-weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly"),
    ONE_TIME("One time"),
    CUSTOM("Custom"),
}

@Serializable
enum class ExpenseCategory(val label: String) {
    HOME("Home"),
    FOOD("Food"),
    HEALTH("Health"),
    TRANSPORT("Transport"),
    ENTERTAINMENT("Entertainment"),
    OTHER("Other"),
}
