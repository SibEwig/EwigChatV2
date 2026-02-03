package com.sibewig.ewigchatv2.domain

data class Chat(
    val id: String,
    val interlocutor: Contact,
    val lastMessage: MessagePreview?
)