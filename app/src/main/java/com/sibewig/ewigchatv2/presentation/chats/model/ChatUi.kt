package com.sibewig.ewigchatv2.presentation.chats.model

import com.sibewig.ewigchatv2.domain.entity.MessagePreview

data class ChatUi(
    val id: String,
    val interlocutorName: String,
    val lastMessage: MessagePreview?
)