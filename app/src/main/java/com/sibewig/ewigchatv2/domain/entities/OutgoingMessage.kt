package com.sibewig.ewigchatv2.domain.entities

data class OutgoingMessage(
    val chatId: String,
    val receiverId: String,
    val text: String
)