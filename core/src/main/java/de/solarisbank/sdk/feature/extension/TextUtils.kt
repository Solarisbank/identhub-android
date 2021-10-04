package de.solarisbank.sdk.feature.extension

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.URLSpan

fun CharSequence.boldOccurrenceOf(string: String): CharSequence {
    return spanOccurrenceOf(string, StyleSpan(Typeface.BOLD))
}

fun CharSequence.linkOccurrenceOf(string: String, link: String): CharSequence {
    return spanOccurrenceOf(string, URLSpan(link))
}

fun CharSequence.spanOccurrenceOf(string: String, span: Any): CharSequence {
    val spannable = if (this is SpannableString) {
        this
    } else {
        SpannableString(this)
    }
    val index = spannable.toString().indexOf(string)
    if (index >= 0) {
        spannable.setSpan(span, index, index + string.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
    return spannable
}