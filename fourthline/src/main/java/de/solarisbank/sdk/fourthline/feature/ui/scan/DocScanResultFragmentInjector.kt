package de.solarisbank.sdk.fourthline.feature.ui.scan

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory


class DocScanResultFragmentInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<DocScanResultFragment> {

    override fun injectMembers(instance: DocScanResultFragment) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(instance: DocScanResultFragment, assistedViewModelFactory: AssistedViewModelFactory) {
            instance.assistedViewModelFactory = assistedViewModelFactory
        }
    }
}