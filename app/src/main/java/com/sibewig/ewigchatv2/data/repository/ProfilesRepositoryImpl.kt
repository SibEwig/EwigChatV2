package com.sibewig.ewigchatv2.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sibewig.ewigchatv2.data.model.ProfileDTO
import com.sibewig.ewigchatv2.data.toDomain
import com.sibewig.ewigchatv2.domain.entity.Profile
import com.sibewig.ewigchatv2.domain.repository.ProfilesRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfilesRepositoryImpl @Inject constructor(
    db: FirebaseFirestore
) : ProfilesRepository {

    private val usersCollection = db.collection("users")
    private val cache = mutableMapOf<String, Profile>()

    override suspend fun getProfile(uid: String): Profile? {
        cache[uid]?.let { return it }
        val snapshot = usersCollection.document(uid).get().await()
        if (!snapshot.exists()) return null

        val dto = snapshot.toObject(ProfileDTO::class.java) ?: return null
        return dto.toDomain().also { cache[uid] = it }
    }

}