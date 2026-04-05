package com.sibewig.ewigchatv2.presentation.chat.model

import androidx.annotation.StringRes

sealed class ChatState {

    data object Loading : ChatState()

    data class Error(@StringRes val msgRes: Int) : ChatState()

    data class Success(val messages: List<MessageUi>, val interlocutorName: String) : ChatState()

    data object Initial : ChatState()

}