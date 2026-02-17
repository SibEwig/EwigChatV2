package com.sibewig.ewigchatv2.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ChatDTO(
    @DocumentId val id: String = "",
    val members: List<String> = emptyList(),
    val lastMessageText: String? = null,
    val lastMessageSenderId: String? = null,
    val lastMessageAt: Timestamp? = null
)
