package de.solarisbank.identhub.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.contract.ContractUiModule
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModelFactory.Companion.create
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.contract.sign.ContractSigningViewModelFactory
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.identhub.domain.contract.*
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCase
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCase
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCase
import de.solarisbank.identhub.identity.IdentityModule
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModel
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModelFactory
import de.solarisbank.identhub.verfication.bank.VerificationBankModule
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModelFactory
import de.solarisbank.identhub.verfication.phone.PhoneVerificationUseCase
import de.solarisbank.identhub.verfication.phone.PhoneVerificationUseCaseImpl
import de.solarisbank.identhub.verfication.phone.PhoneVerificationViewModel
import de.solarisbank.identhub.verfication.phone.PhoneVerificationViewModelFactory
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModel
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModelFactory
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.alert.AlertViewModelFactory
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Provider

@RestrictTo(RestrictTo.Scope.LIBRARY)
class ViewModelMapProvider(
    private val coreModule: CoreModule,
    private val identityModule: IdentityModule,
    private val verificationBankModule: VerificationBankModule,
    private val contractUiModule: ContractUiModule,
    private val authorizeVerificationPhoneUseCaseProvider: Provider<AuthorizeVerificationPhoneUseCase>,
    private val confirmVerificationPhoneUseCaseProvider: Provider<ConfirmVerificationPhoneUseCase>,
    private val getDocumentsFromVerificationBankUseCaseProvider: Provider<GetDocumentsUseCase>,
    private val getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>,
    private val fetchingAuthorizedIBanStatusUseCaseProvider: Provider<FetchingAuthorizedIBanStatusUseCase>,
    private val fetchPdfUseCaseProvider: Provider<FetchPdfUseCase>,
    private val verifyIBanUseCaseProvider: Provider<VerifyIBanUseCase>,
    private val identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
    private val bankIdPostUseCaseProvider: Provider<BankIdPostUseCase>,
    private val processingVerificationUseCaseProvider: Provider<ProcessingVerificationUseCase>,
    private val customizationRepositoryProvider: Provider<CustomizationRepository>,
    private val initializationInfoRepositoryProvider: Provider<InitializationInfoRepository>,
    private val authorizeContractSignUseCaseProvider: Provider<AuthorizeContractSignUseCase>,
    private val confirmContractSignUseCaseProvider: Provider<ConfirmContractSignUseCase>,
    private val getMobileNumberUseCaseProvider: Provider<GetMobileNumberUseCase>,
    private val qesStepParametersRepositoryProvider: Provider<QesStepParametersRepository>
) : Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> {
    override fun get(): Map<Class<out ViewModel>, Provider<ViewModel>> {
        val map: MutableMap<Class<out ViewModel>, Provider<ViewModel>> = LinkedHashMap(10)

        //TODO: Inject
        val phoneVerificationUseCaseProvider: Provider<PhoneVerificationUseCase> = Provider {
            PhoneVerificationUseCaseImpl(
                authorizeVerificationPhoneUseCaseProvider.get(),
                confirmVerificationPhoneUseCaseProvider.get(),
                getMobileNumberUseCaseProvider.get()
            )
        }
        map[ContractSigningPreviewViewModel::class.java] = create(
            contractUiModule = contractUiModule,
            getDocumentsFromVerificationBankUseCaseProvider = getDocumentsFromVerificationBankUseCaseProvider,
            fetchPdfUseCaseProvider = fetchPdfUseCaseProvider,
            getIdentificationUseCaseProvider = getIdentificationUseCaseProvider,
            fetchingAuthorizedIBanStatusUseCaseProvider = fetchingAuthorizedIBanStatusUseCaseProvider
        )
        map[ContractSigningViewModel::class.java] = ContractSigningViewModelFactory(
            contractUiModule,
            authorizeContractSignUseCaseProvider,
            confirmContractSignUseCaseProvider,
            getMobileNumberUseCaseProvider
        )
        map[PhoneVerificationViewModel::class.java] = PhoneVerificationViewModelFactory(
            identityModule, phoneVerificationUseCaseProvider
        )
        map[VerificationPhoneSuccessViewModel::class.java] =
            VerificationPhoneSuccessViewModelFactory.create(
                identityModule
            )
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