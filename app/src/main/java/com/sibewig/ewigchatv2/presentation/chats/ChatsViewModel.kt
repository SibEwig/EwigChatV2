package com.sibewig.ewigchatv2.presentation.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.entity.Chat
import com.sibewig.ewigchatv2.domain.exceptions.CannotStartChatWithYourselfException
import com.sibewig.ewigchatv2.domain.exceptions.ProfileNotFoundException
import com.sibewig.ewigchatv2.domain.exceptions.UnauthorizedException
import com.sibewig.ewigchatv2.domain.usecases.GetAuthStateUseCase
import com.sibewig.ewigchatv2.domain.usecases.GetProfileByUidUseCase
import com.sibewig.ewigchatv2.domain.usecases.ObserveChatsUseCase
import com.sibewig.ewigchatv2.domain.usecases.ResolveDirectChatIdUseCase
import com.sibewig.ewigchatv2.presentation.chats.model.ChatUi
import com.sibewig.ewigchatv2.presentation.chats.model.ChatsEvent
import com.sibewig.ewigchatv2.presentation.chats.model.ChatsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val observeChatsUseCase: ObserveChatsUseCase,
    private val getProfileByUidUseCase: GetProfileByUidUseCase,
    private val resolveDirectChatIdUseCase: ResolveDirectChatIdUseCase,
    getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<ChatsEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatsState: StateFlow<ChatsState> = getAuthStateUseCase().flatMapLatest { authState ->
        when (authState) {
            is AuthState.Authorized -> {
                observeChatsUseCase(authState.userID)
                    .map<List<Chat>, ChatsState> { chats ->
                        val chatUiList = chats.map { chat ->
                            val interlocutorName = try {
                                getProfileByUidUseCase(chat.interlocutorId)?.displayName
                                    ?: "Unknown name"
                            } catch (e: Exception) {
                                "Unknown name"
                            }
                            ChatUi(
                                id = chat.id,
                                interlocutorName = interlocutorName,
                                lastMessage = chat.lastMessage
                            )
                        }
                        ChatsState.Success(chatUiList)
                    }
                    .onStart { emit(ChatsState.Loading) }
                    .catch {e ->
                        if (e is CancellationException) throw e
                        emit(ChatsState.Error(e.toUserMessage()))
                    }
            }

            else -> flowOf(ChatsState.Error("Not authorized"))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ChatsState.Initial
    )

    fun startChatWithUser(username: String) {
        viewModelScope.launch {
            try {
                val chatId = resolveDirectChatIdUseCase(username)
                _events.emit(ChatsEvent.OpenChat(chatId))
            } catch (e: Exception) {
                _events.emit(ChatsEvent.ShowStartChatError(e.toUserMessage()))
            }
        }
    }

    private fun Throwable.toUserMessage(): String {
        return when (this) {
            is ProfileNotFoundException -> "Пользователь не найден"
            is CannotStartChatWithYourselfException -> "Нельзя начать чат с самим собой"
            is UnauthorizedException -> "Сессия истекла. Войдите снова"
            is FirebaseFirestoreException -> "Не удалось загрузить чаты"
            else -> "Произошла ошибка"
        }
    }

    companion object {

        private const val STOP_TIMEOUT_MILLIS = 5000L

    }
}