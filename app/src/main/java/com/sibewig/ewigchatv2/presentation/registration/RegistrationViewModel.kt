package com.sibewig.ewigchatv2.presentation.registration

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.domain.exceptions.UsernameIsTakenException
import com.sibewig.ewigchatv2.domain.usecases.RegisterUseCase
import com.sibewig.ewigchatv2.presentation.registration.model.RegistrationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Initial)
    val registrationState = _registrationState.asStateFlow()

    fun onRegister(
        username: String,
        email: String,
        password: String,
        repeatPassword: String,
        displayName: String
    ) {

        val trimmedUsername = username.trim()
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()
        val trimmedRepeatPassword = repeatPassword.trim()
        val trimmedDisplayName = displayName.trim()

        val validation = validateInput(
            email = trimmedEmail,
            password = trimmedPassword,
            repeatPassword = trimmedRepeatPassword,
            displayName = trimmedDisplayName,
            username = trimmedUsername
        )

        if (validation != null) {
            _registrationState.value = validation
            return
        }

        viewModelScope.launch {
            _registrationState.emit(RegistrationState.Loading)
            try {
                registerUseCase(
                    username = trimmedUsername,
                    email = trimmedEmail,
                    password = trimmedPassword,
                    displayName = trimmedDisplayName,
                    photoUrl = null
                )
                _registrationState.emit(RegistrationState.Success(trimmedEmail))
            } catch (e: Exception) {
                _registrationState.emit(
                    RegistrationState.Error(
                        mapAuthError(e)
                    )
                )
            }
        }
    }

    private fun validateInput(
        username: String,
        email: String,
        password: String,
        repeatPassword: String,
        displayName: String
    ): RegistrationState.InputError? {

        val usernameError = if (username.isBlank()) R.string.error_username_blank
        else if (!isUsernameValid(username)) R.string.error_username_invalid
        else null

        val emailError = if (email.isBlank()) R.string.error_email_blank
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) R.string.error_email_invalid
        else null

        val passwordError = if (password.isBlank()) R.string.error_password_blank
        else if (password.length < 6) R.string.error_password_weak
        else null

        val repeatPasswordError = if (repeatPassword.isBlank()) R.string.error_repeat_password_blank
        else if (repeatPassword != password) R.string.error_password_mismatch
        else null

        val displayNameError = if (displayName.isBlank()) R.string.error_display_name_blank
        else if (displayName.length !in DISPLAY_NAME_LENGTH_RANGE) R.string.error_display_name_length
        else null

        return if (usernameError == null && emailError == null && passwordError == null &&
            repeatPasswordError == null && displayNameError == null
        ) null
        else RegistrationState.InputError(
            usernameErrorRes = usernameError,
            emailErrorRes = emailError,
            passwordErrorRes = passwordError,
            repeatPasswordErrorRes = repeatPasswordError,
            displayNameErrorRes = displayNameError
        )
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.all {
            ((it in 'a'..'z') ||
                    (it in 'A'..'Z') ||
                    (it in '0'..'9') ||
                    it == '_')
        } && username.length in USERNAME_LENGTH_RANGE
    }

    private fun mapAuthError(e: Exception): Int = when (e) {

        is FirebaseAuthUserCollisionException ->
            R.string.error_auth_email_taken

        is FirebaseNetworkException ->
            R.string.error_network

        is UsernameIsTakenException ->
            R.string.error_username_taken

        else ->
            R.string.error_auth_register_failed
    }

    companion object {

        private val USERNAME_LENGTH_RANGE = 3..20
        private val DISPLAY_NAME_LENGTH_RANGE = 3..25
    }
}