package de.solarisbank.sdk.fourthline.feature.ui.terms.welcome

import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

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