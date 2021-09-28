package de.solarisbank.sdk.fourthline.feature.ui.selfie

import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class SelfieFragmentInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>,
        private val customizationRepositoryProvider: Provider<CustomizationRepository>
) : MembersInjector<SelfieFragment> {
    override fun injectMembers(instance: SelfieFragment) {
        instance.assistedViewModelFactory = viewModelFactoryProvider.get()
        instance.customizationRepository = customizationRepositoryProvider.get()
    }
}