package com.sibewig.ewigchatv2.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.domain.AuthState
import com.sibewig.ewigchatv2.domain.entity.AppLanguage
import com.sibewig.ewigchatv2.domain.entity.Settings
import com.sibewig.ewigchatv2.domain.entity.ThemeMode
import com.sibewig.ewigchatv2.domain.usecases.GetAuthStateUseCase
import com.sibewig.ewigchatv2.domain.usecases.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    val authState = getAuthStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AuthState.Initial
        )

    val settings = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Settings(
                themeMode = ThemeMode.SYSTEM,
                appLanguage = AppLanguage.RU
            )
        )
}