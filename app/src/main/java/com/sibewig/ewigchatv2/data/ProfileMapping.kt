package com.sibewig.ewigchatv2.data

import com.sibewig.ewigchatv2.data.model.ProfileDTO
import com.sibewig.ewigchatv2.domain.entity.Profile

fun ProfileDTO.toDomain(id: String): Profile {
    return Profile(
        id = id,
        displayName = displayName ?: "Unknown name",
        avatarUrl = avatarUrl
    )
}