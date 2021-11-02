package de.solarisbank.sdk.core_ui.feature.view

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach

fun ViewGroup.deepForEach(function: View.() -> Unit) {
    this.forEach { child ->
        child.function()
        if (child is ViewGroup) {
            child.deepForEach(function)
        }
    }
}

fun Number.dpToPixels(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        (this).toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun Number.spToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}
