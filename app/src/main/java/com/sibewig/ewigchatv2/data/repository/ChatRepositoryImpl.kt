package com.sibewig.ewigchatv2.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.sibewig.ewigchatv2.data.model.ChatDTO
import com.sibewig.ewigchatv2.data.model.MessageDTO
import com.sibewig.ewigchatv2.data.mapper.toDomain
import com.sibewig.ewigchatv2.data.remote.firestore.ChatFields.LAST_MESSAGE_AT
import com.sibewig.ewigchatv2.data.remote.firestore.ChatFields.LAST_MESSAGE_SENDER_ID
import com.sibewig.ewigchatv2.data.remote.firestore.ChatFields.LAST_MESSAGE_TEXT
import com.sibewig.ewigchatv2.data.remote.firestore.ChatFields.MEMBERS
import com.sibewig.ewigchatv2.data.remote.firestore.ChatFields.UPDATED_AT
import com.sibewig.ewigchatv2.data.remote.firestore.FirestoreCollections.CHATS
import com.sibewig.ewigchatv2.data.remote.firestore.FirestoreCollections.MESSAGES
import com.sibewig.ewigchatv2.data.remote.firestore.MessageFields.CREATED_AT
import com.sibewig.ewigchatv2.data.remote.firestore.MessageFields.SENDER_ID
import com.sibewig.ewigchatv2.data.remote.firestore.MessageFields.TEXT
import com.sibewig.ewigchatv2.domain.entity.Chat
import com.sibewig.ewigchatv2.domain.entity.Message
import com.sibewig.ewigchatv2.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    db: FirebaseFirestore
) : ChatRepository {

    private val chatsCollection = db.collection(CHATS)

    override suspend fun sendMessage(chatId: String, msg: String) {
        val user = auth.currentUser
            ?: throw IllegalStateException(ERROR_CURRENT_USER_NULL)

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
        val msgRef = chatRef.collection(MESSAGES).document()

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
            .collection(MESSAGES)
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
        require(myUid != otherUid) { ERROR_CANNOT_CREATE_CHAT_WITH_SELF }

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
        require(myUid != otherUid) { ERROR_CANNOT_FIND_CHAT_WITH_SELF }

        val normalizedChatId = normalizeChatId("$myUid$CHAT_ID_DELIMITER$otherUid")
        val snapshot = chatsCollection
            .document(normalizedChatId)
            .get()
            .await()

        if (!snapshot.exists()) return null

        val dto = snapshot.toObject(ChatDTO::class.java) ?: return null
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
        require(parts.size == 2) { ERROR_INVALID_CHAT_ID_FORMAT.format(chatId) }
        return parts.sorted().joinToString(CHAT_ID_DELIMITER)
    }

    companion object {

        private const val CHAT_ID_DELIMITER = "_"
        private const val ERROR_INVALID_CHAT_ID_FORMAT = "Invalid chatId format: %s"
        private const val ERROR_CANNOT_FIND_CHAT_WITH_SELF = "Cannot find chat with yourself"
        private const val ERROR_CANNOT_CREATE_CHAT_WITH_SELF = "Cannot create chat with yourself"
        private const val ERROR_CURRENT_USER_NULL = "Current user is null"
    }
}