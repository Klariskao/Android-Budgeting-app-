package com.example.mybudget.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import com.example.mybudget.data.local.BudgetDatabase
import com.example.mybudget.data.local.ExpenseDao
import com.example.mybudget.data.local.IncomeDao
import com.example.mybudget.data.local.SettingsDataStore
import com.example.mybudget.repository.BudgetRepository
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.AddExpenseViewModel
import com.example.mybudget.ui.AddIncomeViewModel
import com.example.mybudget.ui.BudgetViewModel
import com.example.mybudget.ui.ExpenseDetailViewModel
import com.example.mybudget.ui.SharedBudgetViewModel
import com.example.mybudget.ui.ThemeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
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

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = { get<Context>().preferencesDataStoreFile("user_settings") },
        )
    }

    single { SettingsDataStore(get()) }

    viewModel { BudgetViewModel(get()) }
    viewModel { AddExpenseViewModel(get()) }
    viewModel { AddIncomeViewModel(get()) }
    viewModel<ExpenseDetailViewModel> { (savedStateHandle: SavedStateHandle) ->
        ExpenseDetailViewModel(savedStateHandle, get())
    }

    viewModel { ThemeViewModel(get()) }
    viewModel { SharedBudgetViewModel() }
}
