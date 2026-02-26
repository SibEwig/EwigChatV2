package com.sibewig.ewigchatv2.data.model

import com.google.firebase.firestore.DocumentId

data class ProfileDTO(
    @DocumentId val id: String = "",
    val displayName: String? = null,
    val avatarUrl: String? = null
)