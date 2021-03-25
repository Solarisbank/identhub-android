package de.solarisbank.identhub.fourthline.terms

import de.solarisbank.identhub.AssistedViewModelFactory
import de.solarisbank.identhub.di.internal.MembersInjector
import de.solarisbank.identhub.di.internal.Provider

class TermsAndConditionsInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<TermsAndConditionsFragment> {
    override fun injectMembers(instance: TermsAndConditionsFragment) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(instance: TermsAndConditionsFragment, saveStateViewModelFactory: AssistedViewModelFactory?) {
            instance.assistedViewModelFactory = saveStateViewModelFactory!!
        }
    }
}