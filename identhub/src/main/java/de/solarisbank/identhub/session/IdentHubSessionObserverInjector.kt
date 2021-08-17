package de.solarisbank.identhub.session

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

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
//                ,
//                saveStateViewModelMapProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>>
        ) {
            instance.viewModelFactory =  { assistedViewModelFactory.create(it, null) }
        }
    }
}