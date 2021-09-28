package de.solarisbank.sdk.feature.alert

import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class AlertViewModelFactory(
    private val coreModule: CoreModule,
    private val customizationRepositoryProvider: Provider<CustomizationRepository>
): Factory<ViewModel> {

    override fun get(): ViewModel {
        return coreModule.provideAlertViewModel(customizationRepositoryProvider)
    }

}