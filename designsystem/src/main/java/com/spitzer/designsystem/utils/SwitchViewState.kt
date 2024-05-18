package com.spitzer.designsystem.utils

data class SwitchViewState(
    val text: String,
    val isChecked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)
