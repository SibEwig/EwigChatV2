package com.sibewig.ewigchatv2.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class MessageDTO(
    @DocumentId val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val createdAt: Timestamp? = null
)
