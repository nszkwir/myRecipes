package com.spitzer.designsystem.components.error

import com.spitzer.designsystem.components.CallToActionViewState

data class ErrorViewState(
    val subtitle: String,
    val primaryButtonViewState: CallToActionViewState,
    val secondaryButtonViewState: CallToActionViewState? = null
)