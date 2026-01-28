package com.sibewig.ewigchatv2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.domain.AuthRepository
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.usecases.LoginUseCase
import com.sibewig.ewigchatv2.domain.usecases.LogoutUseCase
import com.sibewig.ewigchatv2.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    authRepository: AuthRepository
) : ViewModel() {

    val authState = authRepository.authState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.Initial
        )

    fun onRegister(email: String, password: String) {
        viewModelScope.launch {
            registerUseCase(email, password)
        }
    }

    fun onLogin(email: String, password: String) {
        viewModelScope.launch {
            loginUseCase(email,password)
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}