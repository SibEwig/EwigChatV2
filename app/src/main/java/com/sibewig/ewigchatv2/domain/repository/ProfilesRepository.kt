package com.sibewig.ewigchatv2.domain.repository

import com.sibewig.ewigchatv2.domain.entity.Profile

interface ProfilesRepository {

    suspend fun getProfile(uid: String): Profile?

    suspend fun isUsernameAvailable(username: String): Boolean

    suspend fun createProfile(uid: String, email: String, displayName: String, avatarUrl: String?, username: String)

    suspend fun updateProfile(profile: Profile)

    suspend fun deleteProfile(uid: String)
}