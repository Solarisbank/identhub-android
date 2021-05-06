package de.solarisbank.sdk.fourthline.feature.ui.scan

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

class DocScanFragmentInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<DocScanFragment> {

    override fun injectMembers(instance: DocScanFragment) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(instance: DocScanFragment, assistedViewModelFactory: AssistedViewModelFactory) {
            instance.assistedViewModelFactory = assistedViewModelFactory
        }
    }
}