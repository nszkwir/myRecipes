package com.spitzer.designsystem.components

data class Action(
    val action: (() -> Unit)? = null,
    val description: String?
)
