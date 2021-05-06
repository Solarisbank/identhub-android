package de.solarisbank.sdk.fourthline.feature.ui

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

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