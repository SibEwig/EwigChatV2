package com.sibewig.ewigchatv2.domain

sealed interface AuthState {

    data class Authorized(val userID: String): AuthState

    data object Unauthorized: AuthState

    data object Initial: AuthState
}