package de.solarisbank.identhub.contract.preview;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.contract.ContractModule;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Provider;

public class ContractSigningPreviewViewModelFactory implements Factory<ViewModel> {
    private final ContractModule contractModule;
    private final Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider;
    private final Provider<FetchPdfUseCase> fetchPdfUseCaseProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;

    public ContractSigningPreviewViewModelFactory(ContractModule contractModule,
                                                  Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
                                                  Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
                                                  Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider) {
        this.contractModule = contractModule;
        this.getDocumentsFromVerificationBankUseCaseProvider = getDocumentsFromVerificationBankUseCaseProvider;
        this.fetchPdfUseCaseProvider = fetchPdfUseCaseProvider;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
    }

    public static ContractSigningPreviewViewModelFactory create(ContractModule contractModule,
                                                                Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
                                                                Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
                                                                Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider) {
        return new ContractSigningPreviewViewModelFactory(contractModule, getDocumentsFromVerificationBankUseCaseProvider, fetchPdfUseCaseProvider, getIdentificationUseCaseProvider);
    }

    @Override
    public ViewModel get() {
        return contractModule.provideContractSigningPreviewViewModel(getDocumentsFromVerificationBankUseCaseProvider.get(), fetchPdfUseCaseProvider.get(), getIdentificationUseCaseProvider.get());
    }
}
