package de.solarisbank.sdk.data.dto

import android.content.Context
import de.solarisbank.sdk.feature.view.customization.getThemeColor

data class Customization(
    val enabled: Boolean,
    val colors: Colors,
    val dimens: Dimens,
) {

    data class Colors(
        val primary: Int,
        val secondary: Int,
        val primaryDark: Int,
        val secondaryDark: Int
    ) {

        fun themePrimary(context: Context) = getThemeColor(context, primary, primaryDark)

        fun themeSecondary(context: Context) = getThemeColor(context, secondary, secondaryDark)

    }

    data class Dimens(
        val buttonRadius: Float
    )

}