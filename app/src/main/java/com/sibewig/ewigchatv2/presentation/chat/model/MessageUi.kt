package com.sibewig.ewigchatv2.presentation.chat.model

data class MessageUi(
    val id: String,
    val text: String,
    val timestamp: Long,
    val isMine: Boolean
)
