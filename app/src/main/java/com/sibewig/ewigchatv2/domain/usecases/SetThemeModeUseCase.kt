package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.entity.ThemeMode
import com.sibewig.ewigchatv2.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {

    suspend operator fun invoke(themeMode: ThemeMode) {
        repository.setThemeMode(themeMode)
    }
}