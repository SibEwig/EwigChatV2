package com.sibewig.ewigchatv2.presentation.chats.model

sealed class ChatsState {

    data object Loading : ChatsState()

    data class Error(val msg: String) : ChatsState()

    data class Success(val chats: List<ChatUi>) : ChatsState()

    data object Initial : ChatsState()
}