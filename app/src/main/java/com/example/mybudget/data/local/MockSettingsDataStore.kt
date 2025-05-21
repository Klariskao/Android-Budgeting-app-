package com.example.mybudget.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MockSettingsDataStore : SettingsDataStore {
    private val _darkThemeFlow = MutableStateFlow(false)
    private val _currencyFlow = MutableStateFlow("USD")

    override val darkThemeFlow: Flow<Boolean> = _darkThemeFlow
    override val currencyFlow: Flow<String> = _currencyFlow

    override suspend fun setDarkTheme(enabled: Boolean) {
        _darkThemeFlow.value = enabled
    }

    override suspend fun setCurrency(currency: String) {
        _currencyFlow.value = currency
    }
}
