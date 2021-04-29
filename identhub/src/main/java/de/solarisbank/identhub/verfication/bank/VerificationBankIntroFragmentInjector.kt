package de.solarisbank.identhub.verfication.bank

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

class VerificationBankIntroFragmentInjector(private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>) : MembersInjector<VerificationBankIntroFragment> {

    override fun injectMembers(instance: VerificationBankIntroFragment) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(verificationBankIntroFragment: VerificationBankIntroFragment, assistedViewModelFactory: AssistedViewModelFactory) {
            verificationBankIntroFragment.assistedViewModelFactory = assistedViewModelFactory
        }
    }
}