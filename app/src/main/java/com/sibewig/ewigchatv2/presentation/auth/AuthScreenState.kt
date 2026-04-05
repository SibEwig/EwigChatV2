package com.sibewig.ewigchatv2.presentation.auth

import androidx.annotation.StringRes

data class AuthScreenState(
    val isLoading: Boolean = false,
    @StringRes val errorRes: Int? = null
)