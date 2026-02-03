package com.sibewig.ewigchatv2.domain.entities

data class MessagePreview(
    val text: String,
    val timestamp: Long,
    val senderId: String
)