package de.solarisbank.identhub.verfication.bank

import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class VerificationBankActivityInjector(
    private val assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory>,
    private val customizationRepositoryProvider: Provider<CustomizationRepository>
    ) :
    MembersInjector<VerificationBankActivity> {
    override fun injectMembers(instance: VerificationBankActivity) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactoryProvider.get())
        injectCustomizationRepository(instance, customizationRepositoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(
            verificationBankActivity: VerificationBankActivity,
            assistedViewModelFactory: AssistedViewModelFactory
        ) {
            verificationBankActivity.assistedViewModelFactory = assistedViewModelFactory
        }

        @JvmStatic
        fun injectCustomizationRepository(
            verificationBankActivity: VerificationBankActivity,
            customizationRepository: CustomizationRepository
        ){
            verificationBankActivity.customizationRepository = customizationRepository
        }
    }
}