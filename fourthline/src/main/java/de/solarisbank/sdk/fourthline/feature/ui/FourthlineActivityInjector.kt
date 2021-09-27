package de.solarisbank.sdk.fourthline.feature.ui

import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class FourthlineActivityInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<FourthlineActivity> {

    override fun injectMembers(instance: FourthlineActivity) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(instance: FourthlineActivity, saveStateViewModelFactory: AssistedViewModelFactory) {
            instance.assistedViewModelFactory = saveStateViewModelFactory;
        }
    }
}