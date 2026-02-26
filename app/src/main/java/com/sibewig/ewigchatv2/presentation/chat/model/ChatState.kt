package com.sibewig.ewigchatv2.presentation.chat.model

sealed class ChatState {

    data object Loading : ChatState()

    data class Error(val msg: String) : ChatState()

    data class Success(val messages: List<MessageUi>, val interlocutorName: String) : ChatState()

    data object Initial : ChatState()

}