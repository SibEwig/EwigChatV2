package com.sibewig.ewigchatv2.domain.entity

data class Profile(
    val id: String,
    val displayName: String,
    val avatarUrl: String? = null
)
