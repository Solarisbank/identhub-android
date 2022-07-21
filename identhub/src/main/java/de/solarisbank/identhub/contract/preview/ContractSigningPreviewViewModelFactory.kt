package de.solarisbank.identhub.contract.preview

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.contract.ContractUiModule
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class ContractSigningPreviewViewModelFactory(
    private val contractUiModule: ContractUiModule,
    private val getDocumentsFromVerificationBankUseCaseProvider: Provider<GetDocumentsUseCase>,
    private val fetchPdfUseCaseProvider: Provider<FetchPdfUseCase>,
    private val getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>,
    private val fetchingAuthorizedIBanStatusUseCaseProvider: Provider<FetchingAuthorizedIBanStatusUseCase>
) : Factory<ViewModel> {
    override fun get(): ViewModel {
        return contractUiModule.provideContractSigningPreviewViewModel(
            getDocumentsFromVerificationBankUseCaseProvider.get(),
            fetchPdfUseCaseProvider.get(),
            getIdentificationUseCaseProvider.get(),
            fetchingAuthorizedIBanStatusUseCaseProvider.get()
        )
    }

    companion object {
        @JvmStatic
        fun create(
            contractUiModule: ContractUiModule,
            getDocumentsFromVerificationBankUseCaseProvider: Provider<GetDocumentsUseCase>,
            fetchPdfUseCaseProvider: Provider<FetchPdfUseCase>,
            getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>,
            fetchingAuthorizedIBanStatusUseCaseProvider: Provider<FetchingAuthorizedIBanStatusUseCase>
        ): ContractSigningPreviewViewModelFactory {
            return ContractSigningPreviewViewModelFactory(
                contractUiModule,
                getDocumentsFromVerificationBankUseCaseProvider,
                fetchPdfUseCaseProvider,
                getIdentificationUseCaseProvider,
                fetchingAuthorizedIBanStatusUseCaseProvider
            )
        }
    }
}