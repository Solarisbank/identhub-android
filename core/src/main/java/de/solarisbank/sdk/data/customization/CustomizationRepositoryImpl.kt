package de.solarisbank.sdk.data.customization

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.data.dto.Customization
import de.solarisbank.sdk.data.dto.StyleDto
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import timber.log.Timber

class CustomizationRepositoryImpl(
    private val context: Context,
    private val initialConfigStorage: InitialConfigStorage
    ): CustomizationRepository {

    var cached: Customization? = null

    override fun get(): Customization {
        return cached ?: createCustomization(initialConfigStorage.get().style).also {
            cached = it
        }
    }

    private fun createCustomization(style: StyleDto? = null): Customization {
        val colors = Customization.Colors(
            primary = getSDKColor(style?.colors?.primary, R.color.identhub_color_primary),
            primaryDark = getSDKColor(
                style?.colors?.primaryDark,
                R.color.identhub_color_primary_dark
            ),
            secondary = getSDKColor(style?.colors?.secondary, R.color.identhub_color_secondary),
            secondaryDark = getSDKColor(
                style?.colors?.secondaryDark,
                R.color.identhub_color_secondary_dark
            )
        )
        val dimens = Customization.Dimens(
            buttonRadius = context.resources.getDimensionPixelSize(R.dimen.identhub_default_corner_radius)
                .toFloat()
        )
        val flags = Customization.Flags(
            shouldShowLargeImages = context.resources.getBoolean(R.bool.identhub_show_large_images)
        )
        return Customization(
            enabled = context.resources.getBoolean(R.bool.identhub_customization_enabled),
            colors = colors,
            dimens = dimens,
            customFlags = flags
        )
    }

    private fun getSDKColor(sdkColor: String?, @ColorRes defaultColor: Int): Int {
        sdkColor?.let {
            try {
                return Color.parseColor(it)
            } catch(error: Throwable) {
                Timber.d("Unknown Color")
            }
        }
        return context.getColor(defaultColor)
    }
}



