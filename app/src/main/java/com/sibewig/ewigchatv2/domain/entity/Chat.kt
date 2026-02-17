package com.sibewig.ewigchatv2.domain.entity

data class Chat(
    val id: String,
    val interlocutor: Contact,
    val lastMessage: MessagePreview?
)