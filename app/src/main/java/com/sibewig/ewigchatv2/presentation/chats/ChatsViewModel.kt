package com.sibewig.ewigchatv2.presentation.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.usecases.GetAuthStateUseCase
import com.sibewig.ewigchatv2.domain.usecases.GetProfileUseCase
import com.sibewig.ewigchatv2.domain.usecases.LogoutUseCase
import com.sibewig.ewigchatv2.domain.usecases.ObserveChatsUseCase
import com.sibewig.ewigchatv2.presentation.chats.model.ChatUi
import com.sibewig.ewigchatv2.presentation.chats.model.ChatsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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
    val chatsState: StateFlow<ChatsState> = getAuthStateUseCase().flatMapLatest { authState ->
        when (authState) {
            is AuthState.Authorized -> {
                observeChatsUseCase(authState.userID)
                    .map { chats ->
                        val chatUiList = chats.map { chat ->
                            val interlocutorName = try {
                                getProfileUseCase(chat.interlocutorId)?.displayName
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
                    .catch {
                        ChatsState.Error(it.toUserMessage())
                    }
                    .onStart { ChatsState.Loading }
            }

            else -> flowOf(ChatsState.Error("Not authorized"))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ChatsState.Initial
    )

    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    private fun Throwable.toUserMessage(): String {
        return when (this) {
            is FirebaseFirestoreException -> "Не удалось загрузить чаты"
            else -> "Произошла ошибка"
        }
    }

    companion object {

        private const val STOP_TIMEOUT_MILLIS = 5000L

    }
}