package com.sibewig.ewigchatv2.domain

data class MessagePreview(
    val text: String,
    val timestamp: Long,
    val senderId: String
)