package de.solarisbank.identhub.domain.contract;

import de.solarisbank.identhub.file.FileController;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.di.internal.Provider;

public class FetchPdfUseCaseFactory implements Factory<FetchPdfUseCase> {

    private final Provider<ContractSignRepository> contractSignRepositoryProvider;
    private final Provider<FileController> fileControllerProvider;

    public FetchPdfUseCaseFactory(Provider<ContractSignRepository> contractSignRepositoryProvider,
                                  Provider<FileController> fileControllerProvider) {
        this.contractSignRepositoryProvider = contractSignRepositoryProvider;
        this.fileControllerProvider = fileControllerProvider;
    }

    public static FetchPdfUseCaseFactory create(Provider<ContractSignRepository> contractSignRepositoryProvider,
                                                Provider<FileController> fileControllerProvider) {
        return new FetchPdfUseCaseFactory(contractSignRepositoryProvider, fileControllerProvider);
    }

    @Override
    public FetchPdfUseCase get() {
        return Preconditions.checkNotNull(
                new FetchPdfUseCase(contractSignRepositoryProvider.get(), fileControllerProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
