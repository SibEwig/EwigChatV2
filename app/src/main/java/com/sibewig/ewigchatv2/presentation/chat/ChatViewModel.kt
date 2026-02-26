package com.sibewig.ewigchatv2.presentation.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.entity.Message
import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import com.sibewig.ewigchatv2.domain.usecases.GetProfileUseCase
import com.sibewig.ewigchatv2.domain.usecases.ObserveMessagesUseCase
import com.sibewig.ewigchatv2.domain.usecases.SendMessageUseCase
import com.sibewig.ewigchatv2.presentation.chat.model.ChatState
import com.sibewig.ewigchatv2.presentation.chat.model.MessageUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    observeMessagesUseCase: ObserveMessagesUseCase,
    savedStateHandle: SavedStateHandle,
    authRepository: AuthRepository,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val chatId =
        savedStateHandle.get<String>(CHAT_ID) ?: throw RuntimeException(
            "chatId is required for ChatViewModel"
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatState: StateFlow<ChatState> =
        authRepository.authState.flatMapLatest { authState ->
            when (authState) {
                is AuthState.Authorized -> flow<ChatState> {
                    val myUid = authState.userID

                    val profileId = chatId
                        .split("_")
                        .firstOrNull { it != myUid }
                        ?: run {
                            emit(ChatState.Error("Bad chatId") )
                            return@flow
                        }

                    val profileName = getProfileUseCase(profileId)?.displayName.orEmpty()

                    emit(ChatState.Initial)
                    emitAll(
                        observeMessagesUseCase(chatId).map { messages ->
                            ChatState.Success(messages.toUi(myUid), profileName)
                        }
                    )
                }

                else -> flowOf(ChatState.Error("Not authorized"))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ChatState.Initial
        )

    private fun List<Message>.toUi(currentUserId: String): List<MessageUI> = map { message ->
        MessageUI(
            id = message.id,
            text = message.text,
            timestamp = message.timestamp,
            isMine = message.senderId == currentUserId
        )
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            sendMessageUseCase(chatId, text)
        }
    }

    companion object {

        private const val CHAT_ID = "chatId"
        private const val STOP_TIMEOUT_MILLIS = 5000L

    }
}