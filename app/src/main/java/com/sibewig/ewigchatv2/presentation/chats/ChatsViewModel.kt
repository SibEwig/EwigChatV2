package com.sibewig.ewigchatv2.presentation.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.entity.Chat
import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import com.sibewig.ewigchatv2.domain.usecases.LogoutUseCase
import com.sibewig.ewigchatv2.domain.usecases.ObserveChatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val observeChatsUseCase: ObserveChatsUseCase,
    private val logoutUseCase: LogoutUseCase,
    authRepository: AuthRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatsState: StateFlow<List<Chat>> = authRepository.authState.flatMapLatest { authState ->
        when(authState) {
            is AuthState.Authorized -> observeChatsUseCase(authState.userID)
            else -> flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}