package de.solarisbank.sdk.feature.extension

import android.widget.Button

fun Button.buttonDisabled(isDisabled: Boolean) {
    alpha = if (isDisabled) 0.5f else 1f
    isEnabled = !isDisabled
    isClickable = !isDisabled
}