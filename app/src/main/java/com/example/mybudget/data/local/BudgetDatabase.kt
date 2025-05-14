package com.example.mybudget.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.Income

@Database(
    entities = [Expense::class, Income::class],
    version = 5
)
@TypeConverters(Converters::class)
abstract class BudgetDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
}
