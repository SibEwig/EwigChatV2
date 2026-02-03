package com.sibewig.ewigchatv2.domain.repository

import com.sibewig.ewigchatv2.domain.entities.Chat
import com.sibewig.ewigchatv2.domain.entities.Message
import com.sibewig.ewigchatv2.domain.entities.OutgoingMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun sendMessage(msg: OutgoingMessage)

    fun observeChats(uid: String): Flow<List<Chat>>

    fun observeMessages(chatID: String): Flow<List<Message>>

}