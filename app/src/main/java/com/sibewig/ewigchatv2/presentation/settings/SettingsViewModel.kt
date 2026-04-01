package com.sibewig.ewigchatv2.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.ewigchatv2.domain.entity.AppLanguage
import com.sibewig.ewigchatv2.domain.entity.Settings
import com.sibewig.ewigchatv2.domain.entity.ThemeMode
import com.sibewig.ewigchatv2.domain.usecases.GetSettingsUseCase
import com.sibewig.ewigchatv2.domain.usecases.LogoutUseCase
import com.sibewig.ewigchatv2.domain.usecases.SetAppLanguageUseCase
import com.sibewig.ewigchatv2.domain.usecases.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
    private val logoutUseCase: LogoutUseCase,
    getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    val settings = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Settings(
                themeMode = ThemeMode.SYSTEM,
                appLanguage = AppLanguage.RU
            )
        )

    fun onThemeModeChanged(themeMode: ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(themeMode)
        }
    }

    fun onAppLanguageChanged(appLanguage: AppLanguage) {
        viewModelScope.launch {
            setAppLanguageUseCase(appLanguage)
        }
    }

    fun onLogout() {
        logoutUseCase()
    }
}