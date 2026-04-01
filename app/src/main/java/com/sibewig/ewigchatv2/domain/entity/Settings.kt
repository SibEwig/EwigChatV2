package com.sibewig.ewigchatv2.domain.entity

data class Settings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appLanguage: AppLanguage = AppLanguage.RU
)
