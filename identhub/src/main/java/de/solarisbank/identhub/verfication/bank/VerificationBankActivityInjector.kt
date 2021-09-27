package de.solarisbank.identhub.verfication.bank

import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class VerificationBankActivityInjector(private val assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory>) :
    MembersInjector<VerificationBankActivity> {
    override fun injectMembers(instance: VerificationBankActivity) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(verificationBankActivity: VerificationBankActivity, assistedViewModelFactory: AssistedViewModelFactory) {
            verificationBankActivity.assistedViewModelFactory = assistedViewModelFactory
        }
    }
}