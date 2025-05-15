package com.example.mybudget.di

import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import com.example.mybudget.data.local.BudgetDatabase
import com.example.mybudget.data.local.ExpenseDao
import com.example.mybudget.data.local.IncomeDao
import com.example.mybudget.repository.BudgetRepository
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.AddExpenseViewModel
import com.example.mybudget.ui.AddIncomeViewModel
import com.example.mybudget.ui.BudgetViewModel
import com.example.mybudget.ui.ExpenseDetailViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        single {
            Room.databaseBuilder(
                androidContext(),
                BudgetDatabase::class.java,
                "budget_db",
            ).fallbackToDestructiveMigration().build()
        }

        single<BudgetRepository> { BudgetRepositoryImpl(get(), get()) }

        single<ExpenseDao> { get<BudgetDatabase>().expenseDao() }
        single<IncomeDao> { get<BudgetDatabase>().incomeDao() }

        viewModel { BudgetViewModel(get()) }
        viewModel { AddExpenseViewModel(get()) }
        viewModel { AddIncomeViewModel(get()) }
        viewModel<ExpenseDetailViewModel> { (savedStateHandle: SavedStateHandle) ->
            ExpenseDetailViewModel(savedStateHandle, get())
        }
    }
