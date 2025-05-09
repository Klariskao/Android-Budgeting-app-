package com.example.mybudget.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mybudget.data.model.Income

@Dao
interface IncomeDao {
    @Insert
    suspend fun insertIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

    @Query("SELECT * FROM incomes")
    suspend fun getAllIncomes(): List<Income>
}
