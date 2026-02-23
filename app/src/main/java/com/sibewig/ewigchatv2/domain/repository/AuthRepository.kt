package com.sibewig.ewigchatv2.domain.repository

import com.sibewig.ewigchatv2.domain.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val authState: Flow<AuthState>

    suspend fun login(email: String, password: String)

    suspend fun register(
        email: String,
        password: String,
        displayName: String?,
        photoUrl: String?
    )

    suspend fun logout()
}