package de.solarisbank.sdk.fourthline.feature.ui.scan

import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class DocScanFragmentInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>,
        private val customizationRepositoryProvider: Provider<CustomizationRepository>
) : MembersInjector<DocScanFragment> {

    override fun injectMembers(instance: DocScanFragment) {
        instance.assistedViewModelFactory = viewModelFactoryProvider.get()
        instance.customizationRepository = customizationRepositoryProvider.get()
    }
}