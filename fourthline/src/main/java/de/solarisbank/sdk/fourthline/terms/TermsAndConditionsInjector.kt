package de.solarisbank.sdk.fourthline.terms

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

class TermsAndConditionsInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<TermsAndConditionsFragment> {
    override fun injectMembers(instance: TermsAndConditionsFragment) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(instance: TermsAndConditionsFragment, saveStateViewModelFactory: AssistedViewModelFactory) {
            instance.assistedViewModelFactory = saveStateViewModelFactory
        }
    }
}