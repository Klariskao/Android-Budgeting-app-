package com.example.mybudget.data.local

import androidx.room.TypeConverter
import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import com.example.mybudget.data.model.IncomeFrequency
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromExpenseType(value: ExpensePriority): String = value.name

    @TypeConverter
    fun toExpenseType(value: String): ExpensePriority = ExpensePriority.valueOf(value)

    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory): String = value.name

    @TypeConverter
    fun toExpenseCategory(value: String): ExpenseCategory = ExpenseCategory.valueOf(value)

    @TypeConverter
    fun fromExpenseFrequency(value: ExpenseFrequency): String = value.name

    @TypeConverter
    fun toExpenseFrequency(value: String): ExpenseFrequency = ExpenseFrequency.valueOf(value)

    @TypeConverter
    fun fromIncomeFrequency(value: IncomeFrequency): String = value.name

    @TypeConverter
    fun toIncomeFrequency(value: String): IncomeFrequency = IncomeFrequency.valueOf(value)

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)
}
