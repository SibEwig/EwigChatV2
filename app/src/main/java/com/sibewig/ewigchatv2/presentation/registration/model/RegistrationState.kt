package com.sibewig.ewigchatv2.presentation.registration.model

sealed class RegistrationState {

    data object Initial : RegistrationState()

    data object Loading : RegistrationState()

    data class Success(val email: String) : RegistrationState()

    data class InputError(
        val usernameError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null,
        val repeatPasswordError: String? = null,
        val displayNameError: String? = null
    ) : RegistrationState()

    data class Error(val error: String) : RegistrationState()
}