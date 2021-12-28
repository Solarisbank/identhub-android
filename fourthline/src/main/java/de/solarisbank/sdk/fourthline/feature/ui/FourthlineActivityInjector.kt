package de.solarisbank.sdk.fourthline.feature.ui

import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class FourthlineActivityInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>,
        private val customizationRepositoryProvider: Provider<CustomizationRepository>
) : MembersInjector<FourthlineActivity> {

    override fun injectMembers(instance: FourthlineActivity) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
        injectCustomizationRepository(instance, customizationRepositoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(
            instance: FourthlineActivity,
            saveStateViewModelFactory: AssistedViewModelFactory
        ) {
            instance.assistedViewModelFactory = saveStateViewModelFactory;
        }

        @JvmStatic
        fun injectCustomizationRepository(
            fourthlineActivity: FourthlineActivity,
            customizationRepository: CustomizationRepository
        ){
            fourthlineActivity.customizationRepository = customizationRepository
        }
    }
}