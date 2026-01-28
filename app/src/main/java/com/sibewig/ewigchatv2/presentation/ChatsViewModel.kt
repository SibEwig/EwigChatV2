package com.sibewig.ewigchatv2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.domain.AuthRepository
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    val authState = authRepository.authState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AuthState.Initial
        )


    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}