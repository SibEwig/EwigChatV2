package com.sibewig.ewigchatv2.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.sibewig.ewigchatv2.R
import com.sibewig.ewigchatv2.domain.entity.AppLanguage
import com.sibewig.ewigchatv2.domain.exceptions.EmailNotVerifiedException
import com.sibewig.ewigchatv2.domain.usecases.LoginUseCase
import com.sibewig.ewigchatv2.domain.usecases.ObserveLanguageUseCase
import com.sibewig.ewigchatv2.domain.usecases.SetAppLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
    observeLanguageUseCase: ObserveLanguageUseCase
) : ViewModel() {

    private val _authScreenState = MutableStateFlow(AuthScreenState())

    val authScreenState = _authScreenState.asStateFlow()

    val language = observeLanguageUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.EN
        )

    fun onLogin(email: String, password: String) {
        viewModelScope.launch {
            _authScreenState.update { it.copy(isLoading = true) }
            try {
                loginUseCase(email, password)
            } catch (e: Exception) {
                _authScreenState.update { it.copy(isLoading = false, errorRes = mapAuthError(e)) }
            }
            _authScreenState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onToggleLanguageClick() {
        viewModelScope.launch {
            val newLanguage = when (language.value) {
                AppLanguage.RU -> AppLanguage.EN
                AppLanguage.EN -> AppLanguage.RU
            }
            setAppLanguageUseCase(newLanguage)
        }
    }

    private fun mapAuthError(e: Exception): Int = when (e) {

        is FirebaseAuthInvalidCredentialsException ->
            R.string.error_invalid_credentials

        is FirebaseAuthInvalidUserException ->
            R.string.error_auth_user_not_found

        is FirebaseNetworkException ->
            R.string.error_network

        is EmailNotVerifiedException ->
            R.string.error_email_not_verified

        else -> R.string.error_auth_generic

    }
}

