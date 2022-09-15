package de.solarisbank.identhub.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCase
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModel
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModelFactory
import de.solarisbank.identhub.verfication.bank.VerificationBankModule
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModelFactory
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.alert.AlertViewModelFactory
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Provider

@RestrictTo(RestrictTo.Scope.LIBRARY)
class ViewModelMapProvider(
    private val coreModule: CoreModule,
    private val verificationBankModule: VerificationBankModule,
    private val verifyIBanUseCaseProvider: Provider<VerifyIBanUseCase>,
    private val bankIdPostUseCaseProvider: Provider<BankIdPostUseCase>,
    private val processingVerificationUseCaseProvider: Provider<ProcessingVerificationUseCase>,
    private val customizationRepositoryProvider: Provider<CustomizationRepository>,
    private val initializationInfoRepositoryProvider: Provider<InitializationInfoRepository>
) : Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> {
    override fun get(): Map<Class<out ViewModel>, Provider<ViewModel>> {
        val map: MutableMap<Class<out ViewModel>, Provider<ViewModel>> = LinkedHashMap(10)
        map[VerificationBankIbanViewModel::class.java] = VerificationBankIbanViewModelFactory(
            verificationBankModule,
            verifyIBanUseCaseProvider,
            bankIdPostUseCaseProvider,
            initializationInfoRepositoryProvider
        )
        map[ProcessingVerificationViewModel::class.java] =
            ProcessingVerificationViewModelFactory.create(
                verificationBankModule, processingVerificationUseCaseProvider
            )
        map[AlertViewModel::class.java] =
            AlertViewModelFactory(coreModule, customizationRepositoryProvider)
        return map
    }
}