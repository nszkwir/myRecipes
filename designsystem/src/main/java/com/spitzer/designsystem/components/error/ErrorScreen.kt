package com.spitzer.designsystem.components.error

import android.content.Context
import com.spitzer.designsystem.R
import com.spitzer.designsystem.components.CallToActionViewState
import com.spitzer.designsystem.components.CallToActionViewStyle
import dagger.hilt.android.qualifiers.ApplicationContext

object ErrorScreen {
    fun noInternetConnection(
        @ApplicationContext applicationContext: Context,
        onTryAgainButtonTap: () -> Unit,
        onCloseButtonTap: (() -> Unit)? = null
    ) = ErrorViewState(
        subtitle = applicationContext.getString(R.string.error_noInternet_subtitle),
        primaryButtonViewState = CallToActionViewState(
            title = applicationContext.getString(R.string.try_again_title),
            onTap = {
                onTryAgainButtonTap()
            }),
        secondaryButtonViewState = onCloseButtonTap?.let {
            CallToActionViewState(
                title = applicationContext.getString(R.string.close),
                style = CallToActionViewStyle.WARNING,
                onTap = {
                    it()
                }
            )
        }
    )

    fun generic(
        @ApplicationContext applicationContext: Context,
        onTryAgainButtonTap: () -> Unit,
        onCloseButtonTap: (() -> Unit)? = null
    ) = ErrorViewState(
        subtitle = applicationContext.getString(R.string.error_generic_subtitle),
        primaryButtonViewState = CallToActionViewState(
            title = applicationContext.getString(R.string.try_again_title),
            onTap = {
                onTryAgainButtonTap()
            }
        ),
        secondaryButtonViewState = onCloseButtonTap?.let {
            CallToActionViewState(
                title = applicationContext.getString(R.string.close),
                style = CallToActionViewStyle.WARNING,
                onTap = {
                    it()
                }
            )
        }
    )
}