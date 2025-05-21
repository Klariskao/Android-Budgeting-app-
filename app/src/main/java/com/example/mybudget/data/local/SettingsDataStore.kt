package com.example.mybudget.data.local

import kotlinx.coroutines.flow.Flow

interface SettingsDataStore {
    val darkThemeFlow: Flow<Boolean>
    val currencyFlow: Flow<String>

    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setCurrency(currency: String)
}
