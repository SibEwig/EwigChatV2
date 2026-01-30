package com.sibewig.ewigchatv2.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.domain.AuthScreenState
import com.sibewig.ewigchatv2.domain.usecases.LoginUseCase
import com.sibewig.ewigchatv2.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _authScreenState = MutableStateFlow(AuthScreenState())
    val authScreenState = _authScreenState.asStateFlow()


    fun onRegister(email: String, password: String) {
        viewModelScope.launch {
            _authScreenState.update { it.copy(isLoading = true) }
            try {
                registerUseCase(email, password)
            } catch (e: Exception) {
                Log.d("AuthViewModel", e.toString())
                _authScreenState.update { it.copy(isLoading = false, error = mapAuthError(e)) }
            }
            _authScreenState.update {
                it.copy(isLoading = false)
            }
        }
    }

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
        is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
            "Слишком слабый пароль (минимум 6 символов)."

        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
            "Неверный email или пароль."

        is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
            "Пользователь не найден. Проверь email."

        is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
            "Этот email уже зарегистрирован."

        is com.google.firebase.FirebaseNetworkException ->
            "Проблема с сетью. Проверь интернет."

        else -> "Не удалось выполнить авторизацию. Попробуй ещё раз."
    }

}