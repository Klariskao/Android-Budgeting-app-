package com.example.mybudget.di

import com.example.mybudget.repository.BudgetRepository
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.BudgetViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        single<BudgetRepository> { BudgetRepositoryImpl() }
        viewModel { BudgetViewModel(get()) }
    }
