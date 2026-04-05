package com.sibewig.ewigchatv2.data.remote.firestore

object FirestoreCollections {
    const val USERS = "users"
    const val CHATS = "chats"
    const val USERNAMES = "usernames"
    const val MESSAGES = "messages"
}

object UserFields {
    const val UID = "uid"
    const val EMAIL = "email"
    const val DISPLAY_NAME = "displayName"
    const val ABOUT = "about"
    const val PHOTO_URL = "photoUrl"
    const val USERNAME = "username"
    const val USERNAME_LOWER = "usernameLower"
    const val CREATED_AT = "createdAt"
    const val LAST_SEEN = "lastSeen"
}

object ChatFields {
    const val MEMBERS = "members"
    const val UPDATED_AT = "updatedAt"
    const val LAST_MESSAGE_TEXT = "lastMessageText"
    const val LAST_MESSAGE_SENDER_ID = "lastMessageSenderId"
    const val LAST_MESSAGE_AT = "lastMessageAt"
}

object MessageFields {
    const val SENDER_ID = "senderId"
    const val TEXT = "text"
    const val CREATED_AT = "createdAt"
}