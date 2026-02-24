package com.sibewig.ewigchatv2.presentation.chat.model

data class MessageUI(
    val id: String,
    val text: String,
    val timestamp: Long,
    val isMine: Boolean
)
