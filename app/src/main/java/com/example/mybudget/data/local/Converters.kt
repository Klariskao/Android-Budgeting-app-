package com.example.mybudget.data.local

import androidx.room.TypeConverter
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpenseType

class Converters {
    @TypeConverter
    fun fromExpenseType(value: ExpenseType): String = value.name

    @TypeConverter
    fun toExpenseType(value: String): ExpenseType = ExpenseType.valueOf(value)

    @TypeConverter
    fun fromFrequency(value: ExpenseFrequency): String = value.name

    @TypeConverter
    fun toFrequency(value: String): ExpenseFrequency = ExpenseFrequency.valueOf(value)
}