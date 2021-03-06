package de.solarisbank.sdk.fourthline.feature.ui.scan

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory


class DocTypeSelectionFragmentInjector (private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<DocTypeSelectionFragment> {

    override fun injectMembers(instance: DocTypeSelectionFragment) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(instance: DocTypeSelectionFragment, assistedViewModelFactory: AssistedViewModelFactory) {
            instance.assistedViewModelFactory = assistedViewModelFactory
        }
    }
}