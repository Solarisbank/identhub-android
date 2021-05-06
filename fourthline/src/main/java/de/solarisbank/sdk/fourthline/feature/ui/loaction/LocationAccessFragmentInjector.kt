package de.solarisbank.sdk.fourthline.feature.ui.loaction

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory


class LocationAccessFragmentInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
): MembersInjector<LocationAccessFragment> {
    override fun injectMembers(instance: LocationAccessFragment) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(instance: LocationAccessFragment, saveStateViewModelFactory: AssistedViewModelFactory?) {
            instance.assistedViewModelFactory = saveStateViewModelFactory!!
        }
    }
}