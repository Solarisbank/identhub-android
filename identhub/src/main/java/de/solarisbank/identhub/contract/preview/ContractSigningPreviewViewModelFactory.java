package de.solarisbank.identhub.contract.preview;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Provider;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.identity.IdentityModule;

public class ContractSigningPreviewViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;
    private final Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider;
    private final Provider<FetchPdfUseCase> fetchPdfUseCaseProvider;

    public ContractSigningPreviewViewModelFactory(IdentityModule identityModule,
                                                  Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
                                                  Provider<FetchPdfUseCase> fetchPdfUseCaseProvider) {
        this.identityModule = identityModule;
        this.getDocumentsFromVerificationBankUseCaseProvider = getDocumentsFromVerificationBankUseCaseProvider;
        this.fetchPdfUseCaseProvider = fetchPdfUseCaseProvider;
    }

    public static ContractSigningPreviewViewModelFactory create(IdentityModule identityModule,
                                                                Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
                                                                Provider<FetchPdfUseCase> fetchPdfUseCaseProvider) {
        return new ContractSigningPreviewViewModelFactory(identityModule, getDocumentsFromVerificationBankUseCaseProvider, fetchPdfUseCaseProvider);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideContractSigningPreviewViewModel(getDocumentsFromVerificationBankUseCaseProvider.get(), fetchPdfUseCaseProvider.get());
    }
}
