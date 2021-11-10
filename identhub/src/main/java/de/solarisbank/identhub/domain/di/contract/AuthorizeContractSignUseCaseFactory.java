package de.solarisbank.identhub.domain.di.contract;

import de.solarisbank.identhub.data.contract.ContractSignRepository;
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

public class AuthorizeContractSignUseCaseFactory implements Factory<AuthorizeContractSignUseCase> {

    private final Provider<ContractSignRepository> contractSignRepositoryProvider;

    public AuthorizeContractSignUseCaseFactory(Provider<ContractSignRepository> contractSignRepositoryProvider) {
        this.contractSignRepositoryProvider = contractSignRepositoryProvider;
    }

    public static AuthorizeContractSignUseCaseFactory create(Provider<ContractSignRepository> contractSignRepositoryProvider) {
        return new AuthorizeContractSignUseCaseFactory(contractSignRepositoryProvider);
    }

    @Override
    public AuthorizeContractSignUseCase get() {
        return Preconditions.checkNotNull(
                new AuthorizeContractSignUseCase(contractSignRepositoryProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
