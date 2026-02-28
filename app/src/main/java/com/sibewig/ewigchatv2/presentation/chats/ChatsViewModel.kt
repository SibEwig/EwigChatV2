package com.sibewig.ewigchatv2.presentation.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import com.sibewig.ewigchatv2.domain.usecases.GetAuthStateUseCase
import com.sibewig.ewigchatv2.domain.usecases.GetProfileUseCase
import com.sibewig.ewigchatv2.domain.usecases.LogoutUseCase
import com.sibewig.ewigchatv2.domain.usecases.ObserveChatsUseCase
import com.sibewig.ewigchatv2.presentation.chats.model.ChatUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val observeChatsUseCase: ObserveChatsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatsState: StateFlow<List<ChatUi>> = getAuthStateUseCase().flatMapLatest { authState ->
        when(authState) {
            is AuthState.Authorized -> observeChatsUseCase(authState.userID).map {
                it.map { chat ->
                    ChatUi(
                        id = chat.id,
                        interlocutorName = getProfileUseCase(chat.interlocutorId)?.displayName ?: "Unknown name",
                        lastMessage = chat.lastMessage
                    )
                }
            }
            else -> flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    companion object {

        private const val STOP_TIMEOUT_MILLIS = 5000L

    }
}