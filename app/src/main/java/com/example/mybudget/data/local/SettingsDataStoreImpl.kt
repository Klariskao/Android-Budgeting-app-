package com.example.mybudget.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStoreImpl(private val dataStore: DataStore<Preferences>) : SettingsDataStore {

    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val CURRENCY_KEY = stringPreferencesKey("currency")
    }

    override val darkThemeFlow: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[DARK_THEME_KEY] ?: false }

    override val currencyFlow: Flow<String> =
        dataStore.data.map { preferences -> preferences[CURRENCY_KEY] ?: "USD" }

    override suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[DARK_THEME_KEY] = enabled }
    }

    override suspend fun setCurrency(currency: String) {
        dataStore.edit { it[CURRENCY_KEY] = currency }
    }
}
