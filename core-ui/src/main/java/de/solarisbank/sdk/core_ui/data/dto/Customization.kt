package de.solarisbank.sdk.core_ui.data.dto

import android.content.Context
import de.solarisbank.sdk.core_ui.feature.view.customization.getThemeColor

data class Customization(
    val enabled: Boolean,
    val colors: Colors,
    val dimens: Dimens,
    val customFlags: Flags,
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

    data class Flags(
        val shouldShowLargeImages: Boolean
    )

}