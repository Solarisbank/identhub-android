package de.solarisbank.identhub.fourthline.selfie

import de.solarisbank.identhub.AssistedViewModelFactory
import de.solarisbank.identhub.di.internal.MembersInjector
import de.solarisbank.identhub.di.internal.Provider

class SelfieResultFragmentInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<SelfieResultFragment> {
    override fun injectMembers(instance: SelfieResultFragment) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(instance: SelfieResultFragment, saveStateViewModelFactory: AssistedViewModelFactory?) {
            instance.assistedViewModelFactory = saveStateViewModelFactory!!
        }
    }
}