package com.example.mybudget.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val CURRENCY_KEY = stringPreferencesKey("currency")
    }

    val darkThemeFlow: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[DARK_THEME_KEY] ?: false }

    val currencyFlow: Flow<String> =
        dataStore.data.map { preferences -> preferences[CURRENCY_KEY] ?: "USD" }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[DARK_THEME_KEY] = enabled }
    }

    suspend fun setCurrency(currency: String) {
        dataStore.edit { it[CURRENCY_KEY] = currency }
    }
}
