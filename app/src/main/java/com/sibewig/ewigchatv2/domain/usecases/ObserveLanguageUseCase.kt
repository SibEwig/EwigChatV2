package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.entity.AppLanguage
import com.sibewig.ewigchatv2.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository
) {

    operator fun invoke(): Flow<AppLanguage> {
        return repository.getSettings()
            .map { it.appLanguage }
            .distinctUntilChanged()
    }
}