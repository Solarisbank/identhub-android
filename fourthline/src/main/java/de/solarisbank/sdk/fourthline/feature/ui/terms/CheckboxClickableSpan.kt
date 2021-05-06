package de.solarisbank.sdk.fourthline.feature.ui.terms

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View


class CheckboxClickableSpan(private var onClick: (() -> Unit)?) : ClickableSpan() {
    override fun onClick(widget: View) {
        widget.cancelPendingInputEvents()
        onClick?.invoke()
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        // Show links with underlines (optional)
        ds.isUnderlineText = true
    }

    fun clear() {
        onClick = null
    }
}