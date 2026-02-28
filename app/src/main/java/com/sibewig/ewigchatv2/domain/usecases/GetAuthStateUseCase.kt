package com.sibewig.ewigchatv2.domain.usecases

import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(private val repository: AuthRepository) {

    operator fun invoke(): Flow<AuthState> {
        return repository.authState
    }
}