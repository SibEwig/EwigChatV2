package com.sibewig.ewigchatv2.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.sibewig.ewigchatv2.data.model.ChatDTO
import com.sibewig.ewigchatv2.data.model.MessageDTO
import com.sibewig.ewigchatv2.data.toDomain
import com.sibewig.ewigchatv2.domain.entity.Chat
import com.sibewig.ewigchatv2.domain.entity.Message
import com.sibewig.ewigchatv2.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    db: FirebaseFirestore,
    auth: FirebaseAuth
) : ChatRepository {

    private val currentUser = auth.currentUser

    private val chatsCollection = db.collection(COLLECTION_CHATS)

    override suspend fun sendMessage(chatId: String, msg: String) {
        val user = currentUser ?:
            throw Exception("Current user is null")

        val currentServerTime = FieldValue.serverTimestamp()

        val normalizedChatId = normalizeChatId(chatId)

        val members = normalizedChatId.split(CHAT_ID_DELIMITER)

        val msgData = mapOf(
            SENDER_ID to user.uid,
            TEXT to msg,
            CREATED_AT to currentServerTime
        )

        val chatData = mapOf(
            MEMBERS to members,
            UPDATED_AT to currentServerTime,
            LAST_MESSAGE_TEXT to msg,
            LAST_MESSAGE_SENDER_ID to user.uid,
            LAST_MESSAGE_AT to currentServerTime
        )

        val chatRef = chatsCollection.document(normalizedChatId)
        val msgRef = chatRef.collection(COLLECTION_MESSAGES).document()

        chatRef.set(chatData, SetOptions.merge()).await()
        msgRef.set(msgData).await()
    }

    override fun observeChats(myUid: String): Flow<List<Chat>> = callbackFlow {

        val query = chatsCollection
            .whereArrayContains(MEMBERS, myUid)
            .orderBy(UPDATED_AT, Query.Direction.DESCENDING)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val chats = snapshot
                ?.toObjects(ChatDTO::class.java)
                ?.mapNotNull { it.toDomain(myUid) }
                .orEmpty()

            trySend(chats)
        }

        awaitClose { registration.remove() }
    }

    override fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {

        val normalizedChatId = normalizeChatId(chatId)

        val query = chatsCollection
            .document(normalizedChatId)
            .collection(COLLECTION_MESSAGES)
            .orderBy(CREATED_AT)

        val registration = query
            .addSnapshotListener { snapshot, error -> 
                if (error != null) {
                    close(error) 
                    return@addSnapshotListener
                }

                val messages = snapshot 
                    ?.toObjects(MessageDTO::class.java)
                    ?.map { it.toDomain(normalizedChatId) }
                    .orEmpty() 

                trySend(messages) 
            }

        awaitClose { registration.remove() } 
    }

    override suspend fun createDirectChat(
        myUid: String,
        otherUid: String
    ): String {
        require(myUid != otherUid) { "Cannot create chat with yourself" }

        val normalizedChatId = normalizeChatId("$myUid$CHAT_ID_DELIMITER$otherUid")
        val currentServerTime = FieldValue.serverTimestamp()

        val chatData = mapOf(
            MEMBERS to normalizedChatId.split(CHAT_ID_DELIMITER),
            UPDATED_AT to currentServerTime,
            LAST_MESSAGE_TEXT to null,
            LAST_MESSAGE_SENDER_ID to null,
            LAST_MESSAGE_AT to null
        )

        chatsCollection
            .document(normalizedChatId)
            .set(chatData, SetOptions.merge())
            .await()

        return normalizedChatId
    }

    override suspend fun findDirectChat(
        myUid: String,
        otherUid: String
    ): Chat? {
        require(myUid != otherUid) { "Cannot find chat with yourself" }

        val normalizedChatId = normalizeChatId("$myUid$CHAT_ID_DELIMITER$otherUid")
        Log.d("START_CHAT", "before findDirectChat")
        val snapshot = chatsCollection
            .document(normalizedChatId)
            .get()
            .await()

        if (!snapshot.exists()) return null

        val dto = snapshot.toObject(ChatDTO::class.java) ?: return null
        Log.d("START_CHAT", "after findDirectChat: ${dto.toDomain(myUid)}")
        return dto.toDomain(myUid)
    }

    override suspend fun isChatCreated(chatId: String): Boolean {
        val normalizedChatId = normalizeChatId(chatId)
        return chatsCollection
            .document(normalizedChatId)
            .get()
            .await()
            .exists()
    }

    private fun normalizeChatId(chatId: String): String {
        val parts = chatId.split(CHAT_ID_DELIMITER)
        require(parts.size == 2) { "Invalid chatId format: $chatId" }
        return parts.sorted().joinToString(CHAT_ID_DELIMITER)
    }

    companion object {

        private const val SENDER_ID = "senderId"
        private const val TEXT = "text"
        private const val CREATED_AT = "createdAt"
        private const val MEMBERS = "members"
        private const val UPDATED_AT = "updatedAt"
        private const val LAST_MESSAGE_TEXT = "lastMessageText"
        private const val LAST_MESSAGE_SENDER_ID = "lastMessageSenderId"
        private const val LAST_MESSAGE_AT = "lastMessageAt"
        private const val CHAT_ID_DELIMITER = "_"

        private const val COLLECTION_CHATS = "chats"
        private const val COLLECTION_MESSAGES = "messages"
    }
}