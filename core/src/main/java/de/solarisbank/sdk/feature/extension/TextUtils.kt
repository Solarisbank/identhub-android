package de.solarisbank.sdk.feature.extension

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.URLSpan

fun CharSequence.boldOccurrenceOf(string: String): CharSequence {
    return spanOccurrenceOf(string, StyleSpan(Typeface.BOLD))
}

fun CharSequence.linkOccurrenceOf(string: String, link: String, linkAllIfNotFound: Boolean = false): CharSequence {
    if (contains(string))
        return spanOccurrenceOf(string, URLSpan(link))
    else if (linkAllIfNotFound) {
        return spanOccurrenceOf(toString(), URLSpan(link))
    }
    return this
}

fun CharSequence.spanOccurrenceOf(string: String, span: Any, ignoresCase: Boolean = true): CharSequence {
    val spannable = if (this is SpannableString) {
        this
    } else {
        SpannableString(this)
    }
    val index = spannable.toString().indexOf(string, 0, ignoresCase)
    if (index >= 0) {
        spannable.setSpan(span, index, index + string.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
    return spannable
}