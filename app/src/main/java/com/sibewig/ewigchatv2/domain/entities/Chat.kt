package com.sibewig.ewigchatv2.domain.entities

data class Chat(
    val id: String,
    val interlocutor: Contact,
    val lastMessage: MessagePreview?
)