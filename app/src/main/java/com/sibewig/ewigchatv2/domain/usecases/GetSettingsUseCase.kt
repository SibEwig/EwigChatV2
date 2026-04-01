package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.entity.Settings
import com.sibewig.ewigchatv2.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {

    operator fun invoke(): Flow<Settings> {
        return repository.getSettings()
    }
}