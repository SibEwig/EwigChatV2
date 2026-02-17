package com.sibewig.ewigchatv2.data

import com.sibewig.ewigchatv2.data.model.MessageDTO
import com.sibewig.ewigchatv2.domain.entity.Message

fun MessageDTO.toDomain(chatId: String) = Message(
    id = id,
    senderId = senderId,
    text = text,
    chatId = chatId,
    timestamp = createdAt?.toDate()?.time ?: 0L
)