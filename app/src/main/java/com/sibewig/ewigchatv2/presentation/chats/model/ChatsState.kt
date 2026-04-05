package com.sibewig.ewigchatv2.presentation.chats.model

import androidx.annotation.StringRes

sealed class ChatsState {

    data object Loading : ChatsState()

    data class Error(@StringRes val msgRes: Int) : ChatsState()

    data class Success(val chats: List<ChatUi>) : ChatsState()

    data object Initial : ChatsState()
}