package com.sibewig.ewigchatv2.data

import com.sibewig.ewigchatv2.data.model.ChatDTO
import com.sibewig.ewigchatv2.domain.entity.Chat
import com.sibewig.ewigchatv2.domain.entity.MessagePreview

fun ChatDTO.toDomain(myUid: String): Chat? {
    val interlocutorUid = members.firstOrNull {it != myUid} ?: return null

    val preview = if (
        lastMessageText != null && lastMessageSenderId != null && lastMessageAt != null
    ) MessagePreview(
        text = lastMessageText,
        timestamp = lastMessageAt.toDate().time,
        senderId = lastMessageSenderId
    ) else null

    return Chat(
        id = id,
        interlocutorId = interlocutorUid,
        lastMessage = preview
    )
}