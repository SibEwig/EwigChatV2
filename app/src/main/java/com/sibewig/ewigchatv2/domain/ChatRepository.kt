package com.sibewig.ewigchatv2.domain

import com.sibewig.ewigchatv2.domain.OutgoingMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun sendMessage(msg: OutgoingMessage)

    fun observeChats(uid: String): Flow<List<Chat>>

    fun observeMessages(chatID: String): Flow<List<Message>>

}