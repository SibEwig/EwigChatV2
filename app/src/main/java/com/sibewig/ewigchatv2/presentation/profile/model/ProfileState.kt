package com.sibewig.ewigchatv2.presentation.profile.model

import com.sibewig.ewigchatv2.domain.entity.Profile

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,

    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,

    val editableDisplayName: String = "",
    val editableAbout: String = ""
)