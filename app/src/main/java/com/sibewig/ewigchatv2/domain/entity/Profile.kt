package com.sibewig.ewigchatv2.domain.entity

data class Profile(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val email: String,
    val about: String
)
