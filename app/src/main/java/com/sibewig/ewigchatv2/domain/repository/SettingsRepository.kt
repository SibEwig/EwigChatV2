package com.sibewig.ewigchatv2.domain.repository

import com.sibewig.ewigchatv2.domain.entity.AppLanguage
import com.sibewig.ewigchatv2.domain.entity.Settings
import com.sibewig.ewigchatv2.domain.entity.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getSettings(): Flow<Settings>

    suspend fun setThemeMode(themeMode: ThemeMode)

    suspend fun setAppLanguage(appLanguage: AppLanguage)
}