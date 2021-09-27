package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCase
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.sdk.feature.di.internal.Provider

class VerificationBankIbanViewModelFactory(private val verificationBankModule: VerificationBankModule, private val verifyIBanUseCase: VerifyIBanUseCase, private val bankIdPostUseCase: BankIdPostUseCase) :
    Provider<ViewModel> {

    override fun get(): ViewModel {
        return verificationBankModule.provideVerificationBankIbanViewModel(verifyIBanUseCase, bankIdPostUseCase)
    }

    companion object {
        @JvmStatic
        fun create(verificationBankModule: VerificationBankModule, verifyIBanUseCaseProvider: Provider<VerifyIBanUseCase>, bankIdPostUseCaseProvider: Provider<BankIdPostUseCase>): Provider<ViewModel> {
            return VerificationBankIbanViewModelFactory(verificationBankModule, verifyIBanUseCaseProvider.get(), bankIdPostUseCaseProvider.get())
        }
    }
}
