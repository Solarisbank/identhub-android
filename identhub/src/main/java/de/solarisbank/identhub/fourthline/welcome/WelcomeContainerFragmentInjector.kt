package de.solarisbank.identhub.fourthline.welcome

import de.solarisbank.identhub.AssistedViewModelFactory
import de.solarisbank.identhub.di.internal.MembersInjector
import de.solarisbank.identhub.di.internal.Provider

class WelcomeContainerFragmentInjector(
        val assistedViewModelFactory: Provider<AssistedViewModelFactory>
) : MembersInjector<WelcomeContainerFragment> {

    override fun injectMembers(instance: WelcomeContainerFragment) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(
                instance: WelcomeContainerFragment,
                assistedViewModelFactory: AssistedViewModelFactory) {
            instance.assistedViewModelFactory = assistedViewModelFactory
        }
    }
}