package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String) {
        repository.login(email, password)
    }
}