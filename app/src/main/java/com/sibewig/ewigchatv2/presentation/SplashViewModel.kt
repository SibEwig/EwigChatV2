package com.sibewig.ewigchatv2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.domain.AuthRepository
import com.sibewig.ewigchatv2.domain.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {

    val authState = authRepository.authState
        .stateIn(
            scope = viewModelScope,
            initialValue = AuthState.Initial,
            started = SharingStarted.WhileSubscribed(5_000)
        )
}