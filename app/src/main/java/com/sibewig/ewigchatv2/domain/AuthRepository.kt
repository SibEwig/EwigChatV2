package com.sibewig.ewigchatv2.domain

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val authState: Flow<AuthState>

    suspend fun login(email: String, password: String)

    suspend fun register(email: String, password: String)

    suspend fun logout()
}