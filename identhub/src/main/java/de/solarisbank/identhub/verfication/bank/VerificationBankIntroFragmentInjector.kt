package de.solarisbank.identhub.verfication.bank

import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class VerificationBankIntroFragmentInjector(private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>) :
    MembersInjector<VerificationBankIntroFragment> {

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