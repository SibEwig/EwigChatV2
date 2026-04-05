package com.sibewig.ewigchatv2.data.mapper

import com.sibewig.ewigchatv2.data.model.ProfileDTO
import com.sibewig.ewigchatv2.domain.entity.Profile

fun ProfileDTO.toDomain(id: String): Profile {
    return Profile(
        id = id,
        displayName = checkNotNull(displayName) {
            "Profile displayName is null for id=$id"
        },
        avatarUrl = avatarUrl,
        username = checkNotNull(username) {
            "Profile username is null for id=$id"
        },
        email = checkNotNull(email) {
            "Profile email is null for id=$id"
        },
        about = about.orEmpty()
    )
}