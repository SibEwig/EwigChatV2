package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke() {
        repository.logout()
    }
}