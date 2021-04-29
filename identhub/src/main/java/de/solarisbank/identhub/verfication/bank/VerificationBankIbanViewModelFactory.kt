package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.sdk.core.di.internal.Provider

class VerificationBankIbanViewModelFactory(private val verificationBankModule: VerificationBankModule, private val verifyIBanUseCase: VerifyIBanUseCase) : Provider<ViewModel> {

    override fun get(): ViewModel {
        return verificationBankModule.provideVerificationBankIbanViewModel(verifyIBanUseCase)
    }

    companion object {
        @JvmStatic
        fun create(verificationBankModule: VerificationBankModule, verifyIBanUseCaseProvider: Provider<VerifyIBanUseCase>): Provider<ViewModel> {
            return VerificationBankIbanViewModelFactory(verificationBankModule, verifyIBanUseCaseProvider.get())
        }
    }
}
