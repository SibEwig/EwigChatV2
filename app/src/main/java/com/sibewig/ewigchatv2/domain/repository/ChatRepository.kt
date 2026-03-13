package com.sibewig.ewigchatv2.domain.repository

import com.sibewig.ewigchatv2.domain.entity.Chat
import com.sibewig.ewigchatv2.domain.entity.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun sendMessage(chatId: String, msg: String)

    fun observeChats(myUid: String): Flow<List<Chat>>

    fun observeMessages(chatId: String): Flow<List<Message>>

    suspend fun findDirectChat(myUid: String, otherUid: String): Chat?

    suspend fun createDirectChat(myUid: String, otherUid: String): String

    suspend fun isChatCreated(chatId: String): Boolean
}