package com.sibewig.ewigchatv2.presentation.chats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.domain.usecases.ObserveMessagesUseCase
import com.sibewig.ewigchatv2.domain.usecases.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    observeMessagesUseCase: ObserveMessagesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chatId =
        savedStateHandle.get<String>(CHAT_ID) ?: throw RuntimeException(
            "chatId is required for ChatViewModel"
        )


    val messages = observeMessagesUseCase(chatId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    companion object {

        private const val CHAT_ID = "chatId"
        private const val STOP_TIMEOUT_MILLIS = 5000L

    }
}