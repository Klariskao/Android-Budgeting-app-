package com.example.mybudget.di

import androidx.room.Room
import com.example.mybudget.AddExpenseViewModel
import com.example.mybudget.data.local.ExpenseDatabase
import com.example.mybudget.repository.BudgetRepository
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.BudgetViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        single {
            Room.databaseBuilder(
                androidContext(),
                ExpenseDatabase::class.java,
                "expense_db"
            ).build()
        }

        single { get<ExpenseDatabase>().expenseDao() }

        single<BudgetRepository> { BudgetRepositoryImpl(get()) }

        viewModel { BudgetViewModel(get()) }
        viewModel { AddExpenseViewModel(get()) }
    }
