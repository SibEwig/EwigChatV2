package com.sibewig.ewigchatv2.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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

        val usernameError = if (username.isBlank()) ERROR_USERNAME_BLANK
        else if (!isUsernameValid(username)) ERROR_USERNAME_INVALID
        else null

        val emailError = if (email.isBlank()) ERROR_EMAIL_BLANK
        else if (!email.contains("@")) ERROR_EMAIL_INVALID
        else null

        val passwordError = if (password.isBlank()) ERROR_PASSWORD_BLANK
        else if (password.length < 6) ERROR_PASSWORD_WEAK
        else null

        val repeatPasswordError = if (repeatPassword.isBlank()) ERROR_REPEAT_PASSWORD_BLANK
        else if (repeatPassword != password) ERROR_PASSWORD_MISMATCH
        else null

        val displayNameError = if (displayName.isBlank()) ERROR_DISPLAY_NAME_BLANK
        else if (displayName.length !in DISPLAY_NAME_LENGTH_RANGE) ERROR_DISPLAY_NAME_LENGTH
        else null

        return if (usernameError == null && emailError == null && passwordError == null && repeatPasswordError == null
            && displayNameError == null
        ) null
        else RegistrationState.InputError(
            usernameError = usernameError,
            emailError = emailError,
            passwordError = passwordError,
            repeatPasswordError = repeatPasswordError,
            displayNameError = displayNameError
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

    private fun mapAuthError(e: Exception): String = when (e) {

        is FirebaseAuthUserCollisionException ->
            "Этот email уже зарегистрирован"

        is FirebaseNetworkException ->
            "Проблема с сетью. Проверьте интернет"

        is UsernameIsTakenException -> "Имя пользователя занято"

        else -> "Не удалось выполнить регистрацию. Попробуйте ещё раз"
    }

    companion object {

        private const val ERROR_EMAIL_BLANK = "Email не может быть пустым"
        private const val ERROR_EMAIL_INVALID = "Введите корректный email"
        private const val ERROR_PASSWORD_BLANK = "Пароль не может быть пустым"
        private const val ERROR_PASSWORD_WEAK = "Слабый пароль (мин. 6 символов)"
        private const val ERROR_REPEAT_PASSWORD_BLANK = "Повторите пароль"
        private const val ERROR_PASSWORD_MISMATCH = "Пароли не совпадают"
        private const val ERROR_DISPLAY_NAME_BLANK = "Имя не может быть пустым"
        private const val ERROR_DISPLAY_NAME_LENGTH = "От 3 до 25 символов"
        private const val ERROR_USERNAME_BLANK = "Имя пользователя не может быть пустым"
        private const val ERROR_USERNAME_INVALID = "От 3 до 20 символов: латиница, цифры и _"

        private val USERNAME_LENGTH_RANGE = 3..20
        private val DISPLAY_NAME_LENGTH_RANGE = 3..25
    }
}