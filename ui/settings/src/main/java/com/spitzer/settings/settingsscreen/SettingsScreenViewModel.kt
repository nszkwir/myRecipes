package com.spitzer.settings.settingsscreen

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.spitzer.designsystem.theme.ThemeManager
import com.spitzer.designsystem.utils.SwitchViewState
import com.spitzer.settings.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class SettingsScreenViewModelOutput {
    data class DarkModeToggle(val enabled: Boolean) : SettingsScreenViewModelOutput()
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val themeManager: ThemeManager
) : ViewModel() {

    lateinit var output: (SettingsScreenViewModelOutput) -> Unit

    val viewState: StateFlow<SettingsScreenViewState> by lazy {
        _viewState.asStateFlow()
    }

    private val _viewState by lazy {
        MutableStateFlow(
            SettingsScreenViewState(
                title = mapTitleText(),
                lightDarkModeSwitchViewState = mapLightDarkModeSwitchViewState()
            )
        )
    }

    private fun mapTitleText(): String {
        return applicationContext.getString(
            R.string.settings_screen_title
        )
    }

    private fun mapLightDarkModeSwitchViewState(): SwitchViewState {
        return SwitchViewState(
            text = applicationContext.getString(R.string.settings_screen_light_dark_switch_title),
            isChecked = themeManager.isDarkModeEnabled(),
            onCheckedChange = {
                _viewState.update { currentState ->
                    currentState.copy(
                        lightDarkModeSwitchViewState = currentState.lightDarkModeSwitchViewState.copy(
                            isChecked = it
                        )
                    )
                }
                output(SettingsScreenViewModelOutput.DarkModeToggle(enabled = it))
            }
        )
    }
}