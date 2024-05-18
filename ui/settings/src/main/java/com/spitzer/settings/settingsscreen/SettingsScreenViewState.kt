package com.spitzer.settings.settingsscreen

import com.spitzer.designsystem.utils.SwitchViewState

data class SettingsScreenViewState(
    val title: String,
    val lightDarkModeSwitchViewState: SwitchViewState
)