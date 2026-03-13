package com.sibewig.ewigchatv2.domain.repository

import com.sibewig.ewigchatv2.domain.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val authState: Flow<AuthState>

    suspend fun getCurrentUserUid(): String?

    suspend fun login(email: String, password: String)

    suspend fun register(
        email: String,
        password: String
    ): String

    suspend fun sendVerificationEmail()

    suspend fun isEmailVerified(): Boolean

    fun logout()
}