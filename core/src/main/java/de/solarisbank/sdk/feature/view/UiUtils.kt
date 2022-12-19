package de.solarisbank.sdk.feature.view

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() {
    view?.hideKeyboard()
}

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(windowToken, 0)
}
