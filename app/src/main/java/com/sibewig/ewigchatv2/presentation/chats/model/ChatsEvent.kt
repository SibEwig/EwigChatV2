package com.sibewig.ewigchatv2.presentation.chats.model

sealed interface ChatsEvent {
    data class OpenChat(val chatId: String) : ChatsEvent
    data class ShowStartChatError(val message: String) : ChatsEvent
}