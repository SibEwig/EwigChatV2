package com.sibewig.ewigchatv2.data.model

import com.google.firebase.firestore.DocumentId

data class ProfileDTO(
    @DocumentId val id: String = "",
    val username: String? = null,
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val email: String? = null,
    val about: String? = null
)