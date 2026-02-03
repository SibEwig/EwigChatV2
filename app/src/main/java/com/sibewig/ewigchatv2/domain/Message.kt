package com.sibewig.ewigchatv2.domain

import java.util.Calendar

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long
)