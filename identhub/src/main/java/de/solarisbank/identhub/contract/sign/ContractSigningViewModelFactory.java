package de.solarisbank.identhub.contract.sign;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase;
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.sdk.core.di.internal.Factory2;
import de.solarisbank.sdk.core.di.internal.Provider;

public class ContractSigningViewModelFactory implements Factory2<ViewModel, SavedStateHandle> {
    private final IdentityModule identityModule;
    private final Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider;
    private final Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;

    public ContractSigningViewModelFactory(IdentityModule identityModule,
                                           Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider,
                                           Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider,
                                           Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider) {
        this.identityModule = identityModule;
        this.authorizeContractSignUseCaseProvider = authorizeContractSignUseCaseProvider;
        this.confirmContractSignUseCaseProvider = confirmContractSignUseCaseProvider;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
    }

    public static ContractSigningViewModelFactory create(IdentityModule identityModule,
                                                         Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider,
                                                         Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider,
                                                         Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider) {
        return new ContractSigningViewModelFactory(identityModule, authorizeContractSignUseCaseProvider, confirmContractSignUseCaseProvider, getIdentificationUseCaseProvider);
    }

    @Override
    public ViewModel create(SavedStateHandle savedStateHandle) {
        return identityModule.provideContractSigningViewModel(savedStateHandle, authorizeContractSignUseCaseProvider.get(), confirmContractSignUseCaseProvider.get(), getIdentificationUseCaseProvider.get());
    }
}
