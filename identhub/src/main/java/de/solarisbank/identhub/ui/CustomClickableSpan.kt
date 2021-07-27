package de.solarisbank.identhub.ui

import android.text.style.ClickableSpan
import android.view.View

class CustomClickableSpan(val onClick: (() -> Unit)?): ClickableSpan() {
    override fun onClick(widget: View) {
        widget.cancelPendingInputEvents()
        onClick?.invoke()
    }
}