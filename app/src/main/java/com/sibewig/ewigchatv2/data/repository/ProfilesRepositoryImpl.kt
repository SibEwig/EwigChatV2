package com.sibewig.ewigchatv2.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sibewig.ewigchatv2.data.model.ProfileDTO
import com.sibewig.ewigchatv2.data.toDomain
import com.sibewig.ewigchatv2.domain.entity.Profile
import com.sibewig.ewigchatv2.domain.exceptions.UsernameIsTakenException
import com.sibewig.ewigchatv2.domain.repository.ProfilesRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfilesRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ProfilesRepository {

    private val usersCollection = db.collection(COLLECTION_USERS)
    private val usernamesCollection = db.collection(COLLECTION_USERNAMES)
    private val cache = mutableMapOf<String, Profile>()

    override suspend fun getProfileByUid(uid: String): Profile? {
        cache[uid]?.let { return it }
        val snapshot = usersCollection.document(uid).get().await()
        if (!snapshot.exists()) return null

        val dto = snapshot.toObject(ProfileDTO::class.java) ?: return null
        return dto.toDomain(snapshot.id).also { cache[uid] = it }
    }

    override suspend fun getProfileByUsername(username: String): Profile? {
        val usernameLower = username.trim().lowercase()
        val snapshot = usersCollection
            .whereEqualTo("usernameLower", usernameLower)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?: return null
        return snapshot.toObject(ProfileDTO::class.java)?.toDomain(snapshot.id)
    }

    override suspend fun isUsernameAvailable(username: String): Boolean {
        val usernameLower = username.trim().lowercase()

        val snapshot = usernamesCollection
            .document(usernameLower)
            .get()
            .await()

        return !snapshot.exists()
    }

    override suspend fun createProfile(
        uid: String,
        email: String,
        displayName: String,
        avatarUrl: String?,
        username: String
    ) {
        val usernameLower = username.trim().lowercase()
        val currentServerTime = FieldValue.serverTimestamp()

        val usernameRef = usernamesCollection
            .document(usernameLower)

        val userRef = usersCollection
            .document(uid)

        db.runTransaction { transaction ->
            val usernameSnapshot = transaction.get(usernameRef)

            if (usernameSnapshot.exists()) {
                throw UsernameIsTakenException()
            }

            transaction.set(
                usernameRef,
                mapOf(
                    UID to uid,
                    CREATED_AT to currentServerTime
                )
            )

            transaction.set(
                userRef,
                mapOf(
                    DISPLAY_NAME to displayName,
                    EMAIL to email,
                    PHOTO_URL to avatarUrl,
                    CREATED_AT to currentServerTime,
                    LAST_SEEN to currentServerTime,
                    USERNAME to username,
                    USERNAME_LOWER to usernameLower
                ),
                SetOptions.merge()
            )

            null
        }.await()
    }

    override suspend fun updateProfile(profile: Profile) {
        usersCollection
            .document(profile.id)
            .update(
                mapOf(
                    "displayName" to profile.displayName,
                    "about" to profile.about
                )
            )
            .await()
        cache[profile.id] = profile
    }

    override suspend fun deleteProfile(uid: String) {
        throw UnsupportedOperationException("Delete profile is not implemented in MVP")
    }

    companion object {

        private const val EMAIL = "email"
        private const val UID = "uid"
        private const val DISPLAY_NAME = "displayName"
        private const val PHOTO_URL = "photoUrl"
        private const val CREATED_AT = "createdAt"
        private const val LAST_SEEN = "lastSeen"
        private const val USERNAME = "username"
        private const val USERNAME_LOWER = "usernameLower"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_USERNAMES = "usernames"

    }
}