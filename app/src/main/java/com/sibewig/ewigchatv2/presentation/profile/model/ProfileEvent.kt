package com.sibewig.ewigchatv2.presentation.profile.model

sealed interface ProfileEvent {

    data class ShowError(val msg: String): ProfileEvent
}