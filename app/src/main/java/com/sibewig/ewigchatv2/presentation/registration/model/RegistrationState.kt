package com.sibewig.ewigchatv2.presentation.registration.model

sealed class RegistrationState {

    data object Initial : RegistrationState()

    data object Loading : RegistrationState()

    data class Success(val email: String) : RegistrationState()

    data class InputError(
        val usernameErrorRes: Int? = null,
        val emailErrorRes: Int? = null,
        val passwordErrorRes: Int? = null,
        val repeatPasswordErrorRes: Int? = null,
        val displayNameErrorRes: Int? = null
    ) : RegistrationState()

    data class Error(val errorRes: Int) : RegistrationState()
}