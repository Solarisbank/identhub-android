package de.solarisbank.identhub.verfication.bank

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

class VerificationBankActivityInjector(private val assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory>) : MembersInjector<VerificationBankActivity> {
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