package de.solarisbank.sdk.feature.customization

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.data.dto.StyleDto
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSource
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class CustomizationRepositoryImpl(
    private val context: Context,
    private val initializationInfoRetrofitDataSource: InitializationInfoRetrofitDataSource,
    private val customizationSharedPrefsStore: CustomizationSharedPrefsStore,
    private val sessionUrlRepository: SessionUrlRepository
    ): CustomizationRepository {

    override fun initialize(): Single<Customization> {
        val cached = customizationSharedPrefsStore.get()
        return if (cached != null) {
            refresh()
            Single.just(cached)
        } else {
            fetchCustomization()
        }
    }

    override fun get(): Customization {
        return customizationSharedPrefsStore.get() ?: createCustomization()
    }

    private fun fetchCustomization(): Single<Customization> {
        return initializationInfoRetrofitDataSource.getInfo(sessionUrlRepository.get()!!)
            .map {
                createCustomization(it.style)
            }
            .doOnSuccess {
                customizationSharedPrefsStore.put(it)
            }
    }

    @SuppressLint("CheckResult")
    private fun refresh() {
        fetchCustomization()
            .subscribeOn(Schedulers.io())
            .subscribe({}, {})
    }

    private fun createCustomization(style: StyleDto? = null): Customization {
        val colors = Customization.Colors(
            primary = getSDKColor(style?.colors?.primary, R.color.ident_hub_color_primary),
            primaryDark = getSDKColor(style?.colors?.primaryDark, R.color.ident_hub_color_primary_dark),
            secondary = getSDKColor(style?.colors?.secondary, R.color.ident_hub_color_secondary),
            secondaryDark = getSDKColor(style?.colors?.secondaryDark, R.color.ident_hub_color_secondary_dark)
        )
        val dimens = Customization.Dimens(
            buttonRadius = context.resources.getDimensionPixelSize(R.dimen.default_corner_radius)
                .toFloat()
        )
        val flags = Customization.Flags(
            shouldShowLargeImages = context.resources.getBoolean(R.bool.show_large_images)
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



