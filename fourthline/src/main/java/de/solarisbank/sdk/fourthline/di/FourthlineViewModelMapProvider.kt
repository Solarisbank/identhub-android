package de.solarisbank.sdk.fourthline.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.alert.AlertViewModelFactory
import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.di.internal.Provider

@RestrictTo(RestrictTo.Scope.LIBRARY)
class FourthlineViewModelMapProvider(
    private val coreModule: CoreModule,
    private val customizationRepositoryProvider: Provider<CustomizationRepository>
) : Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> {

    override fun get(): Map<Class<out ViewModel>, Provider<ViewModel>> {
        return linkedMapOf(
            AlertViewModel::class.java to AlertViewModelFactory(
                coreModule,
                customizationRepositoryProvider
            )
        )
    }

}