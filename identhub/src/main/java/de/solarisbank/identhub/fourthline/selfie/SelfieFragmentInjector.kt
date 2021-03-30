package de.solarisbank.identhub.fourthline.selfie

import de.solarisbank.identhub.AssistedViewModelFactory
import de.solarisbank.identhub.di.internal.MembersInjector
import de.solarisbank.identhub.di.internal.Provider

class SelfieFragmentInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<SelfieFragment> {
    override fun injectMembers(instance: SelfieFragment) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(instance: SelfieFragment, saveStateViewModelFactory: AssistedViewModelFactory?) {
            instance.assistedViewModelFactory = saveStateViewModelFactory!!
        }
    }
}