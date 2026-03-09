package com.sibewig.ewigchatv2.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.sibewig.ewigchatv2.domain.exceptions.EmailNotVerifiedException
import com.sibewig.ewigchatv2.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _authScreenState = MutableStateFlow(AuthScreenState())
    val authScreenState = _authScreenState.asStateFlow()

    fun onLogin(email: String, password: String) {
        viewModelScope.launch {
            _authScreenState.update { it.copy(isLoading = true) }
            try {
                loginUseCase(email, password)
            } catch (e: Exception) {
                _authScreenState.update { it.copy(isLoading = false, error = mapAuthError(e)) }
            }
            _authScreenState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun mapAuthError(e: Exception): String = when (e) {

        is FirebaseAuthInvalidCredentialsException ->
            "Неверный email или пароль."

        is FirebaseAuthInvalidUserException ->
            "Пользователь не найден. Проверь email."

        is FirebaseNetworkException ->
            "Проблема с сетью. Проверь интернет."

        is EmailNotVerifiedException ->
            "Пожалуйста, подтвердите свой email."


        else -> "Не удалось выполнить авторизацию. Попробуй ещё раз."
    }

}