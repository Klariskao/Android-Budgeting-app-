package com.example.mybudget.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybudget.data.local.SettingsDataStoreImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(settingsDataStore: SettingsDataStoreImpl) : ViewModel() {
    val isDarkTheme = settingsDataStore.darkThemeFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false,
    )
}
