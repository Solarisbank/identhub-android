package de.solarisbank.sdk.feature.view.customization

import android.content.Context
import android.content.res.Configuration

fun Context.isDarkMode(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

fun getThemeColor(context: Context, lightColor: Int, darkColor: Int): Int {
    return if (context.isDarkMode())
        darkColor
    else
        lightColor
}