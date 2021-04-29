package de.solarisbank.identhub.contract.preview;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.contract.ContractModule;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Provider;

public class ContractSigningPreviewViewModelFactory implements Factory<ViewModel> {
    private final ContractModule contractModule;
    private final Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider;
    private final Provider<FetchPdfUseCase> fetchPdfUseCaseProvider;

    public ContractSigningPreviewViewModelFactory(ContractModule contractModule,
                                                  Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
                                                  Provider<FetchPdfUseCase> fetchPdfUseCaseProvider) {
        this.contractModule = contractModule;
        this.getDocumentsFromVerificationBankUseCaseProvider = getDocumentsFromVerificationBankUseCaseProvider;
        this.fetchPdfUseCaseProvider = fetchPdfUseCaseProvider;
    }

    public static ContractSigningPreviewViewModelFactory create(ContractModule contractModule,
                                                                Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
                                                                Provider<FetchPdfUseCase> fetchPdfUseCaseProvider) {
        return new ContractSigningPreviewViewModelFactory(contractModule, getDocumentsFromVerificationBankUseCaseProvider, fetchPdfUseCaseProvider);
    }

    @Override
    public ViewModel get() {
        return contractModule.provideContractSigningPreviewViewModel(getDocumentsFromVerificationBankUseCaseProvider.get(), fetchPdfUseCaseProvider.get());
    }
}
