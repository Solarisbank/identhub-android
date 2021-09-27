package de.solarisbank.identhub.session.feature.di

import de.solarisbank.identhub.session.feature.IdentHubSessionObserver
import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class IdentHubSessionObserverInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<IdentHubSessionObserver> {

    override fun injectMembers(instance: IdentHubSessionObserver) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(
            instance: IdentHubSessionObserver,
            assistedViewModelFactory: AssistedViewModelFactory
        ) {
            instance.viewModelFactory =  { assistedViewModelFactory.create(it, null) }
        }
    }
}