package de.solarisbank.identhub.domain.contract;

import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.di.internal.Provider;

public class ConfirmContractSignUseCaseFactory implements Factory<ConfirmContractSignUseCase> {

    private final Provider<ContractSignRepository> contractSignRepositoryProvider;

    public ConfirmContractSignUseCaseFactory(Provider<ContractSignRepository> contractSignRepositoryProvider) {
        this.contractSignRepositoryProvider = contractSignRepositoryProvider;
    }

    public static ConfirmContractSignUseCaseFactory create(Provider<ContractSignRepository> contractSignRepositoryProvider) {
        return new ConfirmContractSignUseCaseFactory(contractSignRepositoryProvider);
    }

    @Override
    public ConfirmContractSignUseCase get() {
        return Preconditions.checkNotNull(
                new ConfirmContractSignUseCase(contractSignRepositoryProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
