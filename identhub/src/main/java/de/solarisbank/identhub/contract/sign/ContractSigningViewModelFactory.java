package de.solarisbank.identhub.contract.sign;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.contract.ContractModule;
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase;
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase;
import de.solarisbank.identhub.domain.contract.GetMobileNumberUseCase;
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase;
import de.solarisbank.sdk.feature.di.internal.Factory2;
import de.solarisbank.sdk.feature.di.internal.Provider;

public class ContractSigningViewModelFactory implements Factory2<ViewModel, SavedStateHandle> {
    private final ContractModule contractModule;
    private final Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider;
    private final Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider;
    private final Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider;
    private final Provider<GetMobileNumberUseCase> getMobileNumberUseCaseProvider;

    public ContractSigningViewModelFactory(ContractModule contractModule,
                                           Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider,
                                           Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider,
                                           Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider,
                                           Provider<GetMobileNumberUseCase> getMobileNumberUseCaseProvider
    ) {
        this.contractModule = contractModule;
        this.authorizeContractSignUseCaseProvider = authorizeContractSignUseCaseProvider;
        this.confirmContractSignUseCaseProvider = confirmContractSignUseCaseProvider;
        this.identificationPollingStatusUseCaseProvider = identificationPollingStatusUseCaseProvider;
        this.getMobileNumberUseCaseProvider = getMobileNumberUseCaseProvider;
    }

    public static ContractSigningViewModelFactory create(
            ContractModule contractModule,
            Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider,
            Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider,
            Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider,
            Provider<GetMobileNumberUseCase> getMobileNumberUseCaseProvider
    ) {
        return new ContractSigningViewModelFactory(
                contractModule,
                authorizeContractSignUseCaseProvider,
                confirmContractSignUseCaseProvider,
                identificationPollingStatusUseCaseProvider,
                getMobileNumberUseCaseProvider
        );
    }

    @Override
    public ViewModel create(SavedStateHandle savedStateHandle) {
        return contractModule.provideContractSigningViewModel(
                savedStateHandle,
                authorizeContractSignUseCaseProvider.get(),
                confirmContractSignUseCaseProvider.get(),
                identificationPollingStatusUseCaseProvider.get(),
                getMobileNumberUseCaseProvider.get()
        );
    }
}
