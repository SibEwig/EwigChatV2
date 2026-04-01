package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.entity.AppLanguage
import com.sibewig.ewigchatv2.domain.repository.SettingsRepository
import javax.inject.Inject

class SetAppLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository
) {

    suspend operator fun invoke(appLanguage: AppLanguage) {
        repository.setAppLanguage(appLanguage)
    }
}