package com.sibewig.ewigchatv2.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sibewig.ewigchatv2.domain.entity.AppLanguage
import com.sibewig.ewigchatv2.domain.entity.Settings
import com.sibewig.ewigchatv2.domain.entity.ThemeMode
import com.sibewig.ewigchatv2.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override fun getSettings(): Flow<Settings> {
        return dataStore.data.map { preferences ->
            Settings(
                themeMode = preferences[PreferencesKeys.THEME_MODE].toThemeMode(),
                appLanguage = preferences[PreferencesKeys.APP_LANGUAGE].toAppLanguage()
            )
        }
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    override suspend fun setAppLanguage(appLanguage: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LANGUAGE] = appLanguage.name
        }
    }

    private fun String?.toThemeMode(): ThemeMode {
        return ThemeMode.entries.firstOrNull { it.name == this } ?: ThemeMode.SYSTEM
    }

    private fun String?.toAppLanguage(): AppLanguage {
        return AppLanguage.entries.firstOrNull { it.name == this } ?: AppLanguage.RU
    }

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }
}