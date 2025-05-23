package com.example.mybudget

import android.app.Application
import com.example.mybudget.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyBudgetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyBudgetApp)
            modules(appModule)
        }
    }
}
