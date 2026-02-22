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

    private val chatsCollection = db.collection("chats")

    override suspend fun sendMessage(chatId: String, msg: String) {
        val user = currentUser ?:
            throw Exception("Current user is null")

        val currentServerTime = FieldValue.serverTimestamp()

        val normalizedChatId = normalizeChatId(chatId)

        val members = normalizedChatId.split("_")

        val msgData = mapOf(
            "senderId" to user.uid,
            "text" to msg,
            "createdAt" to currentServerTime
        )

        val chatData = mapOf(
            "members" to members,
            "updatedAt" to currentServerTime,
            "lastMessageText" to msg,
            "lastMessageSenderId" to user.uid,
            "lastMessageAt" to currentServerTime
        )

        val chatRef = chatsCollection.document(normalizedChatId)
        val msgRef = chatRef.collection("messages").document()

        chatRef.set(chatData, SetOptions.merge()).await()
        msgRef.set(msgData).await()
    }

    override fun observeChats(myUid: String): Flow<List<Chat>> = callbackFlow {

        Log.d("ChatRepositoryImpl", "Trying to observe chats for myUid: $myUid")

        val query = chatsCollection
            .whereArrayContains("members", myUid)
            .orderBy("updatedAt", Query.Direction.DESCENDING)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val chats = snapshot
                ?.toObjects(ChatDTO::class.java)
                .also { Log.d("ChatRepositoryImpl", "Chats: $it") }
                ?.mapNotNull { it.toDomain(myUid) }
                .orEmpty()

            trySend(chats)
        }

        awaitClose { registration.remove() }
    }

    override fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {

        val normalizedChatId = normalizeChatId(chatId)

        Log.d("ChatRepositoryImpl", "Trying to observe messages for chatId: $normalizedChatId")

        val query = chatsCollection
            .document(normalizedChatId)
            .collection("messages")
            .orderBy("createdAt")

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

    private fun normalizeChatId(chatId: String): String {
        val parts = chatId.split("_")
        require(parts.size == 2) { "Invalid chatId format: $chatId" }
        return parts.sorted().joinToString("_")
    }
}