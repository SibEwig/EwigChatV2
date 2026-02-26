package com.sibewig.ewigchatv2.domain.entity

data class Chat(
    val id: String,
    val interlocutorId: String,
    val lastMessage: MessagePreview?
)