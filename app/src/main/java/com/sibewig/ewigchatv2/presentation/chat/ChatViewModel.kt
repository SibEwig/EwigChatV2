package com.sibewig.ewigchatv2.presentation.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.entity.Message
import com.sibewig.ewigchatv2.domain.usecases.DoesChatExistUseCase
import com.sibewig.ewigchatv2.domain.usecases.GetAuthStateUseCase
import com.sibewig.ewigchatv2.domain.usecases.GetProfileByUidUseCase
import com.sibewig.ewigchatv2.domain.usecases.ObserveMessagesUseCase
import com.sibewig.ewigchatv2.domain.usecases.SendMessageUseCase
import com.sibewig.ewigchatv2.presentation.chat.model.ChatState
import com.sibewig.ewigchatv2.presentation.chat.model.MessageUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getProfileByUidUseCase: GetProfileByUidUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    getAuthStateUseCase: GetAuthStateUseCase,
    doesChatExistUseCase: DoesChatExistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chatId =
        savedStateHandle.get<String>(CHAT_ID) ?: error("chatId is required for ChatViewModel")

    // True when ChatFragment opened from Chats list.
    // In this case chat definitely exists and we can observe messages immediately.
    // For chats started via username chat may not exist yet.
    private val isExistingChat = savedStateHandle.get<Boolean>(IS_EXISTING_CHAT) ?: false

    private val isChatCreated = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            isChatCreated.value = doesChatExistUseCase(chatId)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatState: StateFlow<ChatState> =
        combine(getAuthStateUseCase(), isChatCreated) { authState, chatCreated ->
            authState to chatCreated
        }
            .flatMapLatest { (authState, chatCreated) ->
                when (authState) {
                    is AuthState.Authorized -> {
                        val myUid = authState.userID

                        buildAuthorizedChatState(myUid, chatCreated)
                            .onStart { emit(ChatState.Loading) }
                            .catch { e ->
                                if (e is CancellationException) throw e
                                emit(ChatState.Error(e.toChatErrorMessage()))
                            }

                    }

                    else -> flowOf(ChatState.Error("Not authorized"))
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = ChatState.Initial
            )

    private fun buildAuthorizedChatState(
        myUid: String,
        chatCreated: Boolean
    ): Flow<ChatState> = flow {
        val profileId = extractInterlocutorId(myUid)
        if (profileId == null) {
            emit(ChatState.Error("Bad ChatId"))
            return@flow
        }
        val profileName = getProfileByUidUseCase(profileId)?.displayName.orEmpty()

        if (isExistingChat || chatCreated) {
            emitAll(
                observeMessagesUseCase(chatId)
                    .map<List<Message>, ChatState> { messages ->
                        ChatState.Success(
                            messages.toUi(myUid),
                            profileName
                        )
                    }
            )
        } else {
            emit(ChatState.Success(emptyList(), profileName))
        }
    }

    private fun extractInterlocutorId(myUid: String): String? {
        val profileId = chatId
            .split(CHAT_ID_DELIMITER)
            .firstOrNull { it != myUid }
        return profileId
    }

    private fun List<Message>.toUi(currentUserId: String): List<MessageUi> = map { message ->
        MessageUi(
            id = message.id,
            text = message.text,
            timestamp = message.timestamp,
            isMine = message.senderId == currentUserId
        )
    }

    private fun Throwable.toChatErrorMessage(): String {
        return when (this) {
            is FirebaseFirestoreException -> when (code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "Нет доступа к чату"

                FirebaseFirestoreException.Code.UNAVAILABLE ->
                    "Не удалось подключиться к серверу"

                else ->
                    "Не удалось загрузить сообщения"
            }

            else -> "Произошла ошибка при загрузке чата"
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            sendMessageUseCase(chatId, text)
            if (!isChatCreated.value) {
                isChatCreated.value = true
            }
        }
    }

    companion object {

        private const val CHAT_ID = "chatId"
        private const val IS_EXISTING_CHAT = "isExistingChat"
        private const val STOP_TIMEOUT_MILLIS = 5000L
        private const val CHAT_ID_DELIMITER = "_"

    }
}