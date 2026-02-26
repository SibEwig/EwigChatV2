package com.sibewig.ewigchatv2.domain.repository

import com.sibewig.ewigchatv2.domain.entity.Profile

interface ProfilesRepository {

    suspend fun getProfile(uid: String): Profile?

}