package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCase
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import de.solarisbank.sdk.feature.di.internal.Provider

class VerificationBankIbanViewModelFactory(
    private val verificationBankModule: VerificationBankModule,
    private val verifyIBanUseCaseProvider: Provider<VerifyIBanUseCase>,
    private val bankIdPostUseCaseProvider: Provider<BankIdPostUseCase>,
    private val initializationInfoRepositoryProvider: Provider<InitializationInfoRepository>
    ) : Provider<ViewModel> {

    override fun get(): ViewModel {
        return verificationBankModule.provideVerificationBankIbanViewModel(
            verifyIBanUseCaseProvider.get(),
            bankIdPostUseCaseProvider.get(),
            initializationInfoRepositoryProvider.get()
        )
    }
}
