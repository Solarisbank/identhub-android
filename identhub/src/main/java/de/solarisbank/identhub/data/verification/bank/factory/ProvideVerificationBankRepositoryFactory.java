package de.solarisbank.identhub.data.verification.bank.factory;

import de.solarisbank.identhub.data.Mapper;
import de.solarisbank.identhub.data.dto.IdentificationDto;
import de.solarisbank.identhub.data.entity.IdentificationWithDocument;
import de.solarisbank.identhub.data.verification.bank.VerificationBankLocalDataSource;
import de.solarisbank.identhub.data.verification.bank.VerificationBankDataModule;
import de.solarisbank.identhub.data.verification.bank.VerificationBankNetworkDataSource;
import de.solarisbank.identhub.domain.verification.bank.VerificationBankRepository;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.di.internal.Provider;

public class ProvideVerificationBankRepositoryFactory implements Factory<VerificationBankRepository> {

    private final VerificationBankDataModule verificationBankDataModule;
    private final Provider<Mapper<IdentificationDto, IdentificationWithDocument>> identificationWithDocumentMapperProvider;
    private final Provider<VerificationBankNetworkDataSource> verificationBankNetworkDataSourceProvider;
    private final Provider<VerificationBankLocalDataSource> verificationBankLocalDataSourceProvider;

    public ProvideVerificationBankRepositoryFactory(
            Provider<Mapper<IdentificationDto, IdentificationWithDocument>> identificationWithDocumentMapperProvider,
            Provider<VerificationBankNetworkDataSource> verificationBankNetworkDataSourceProvider,
            VerificationBankDataModule verificationBankDataModule,
            Provider<VerificationBankLocalDataSource> verificationBankLocalDataSourceProvider) {
        this.identificationWithDocumentMapperProvider = identificationWithDocumentMapperProvider;
        this.verificationBankDataModule = verificationBankDataModule;
        this.verificationBankNetworkDataSourceProvider = verificationBankNetworkDataSourceProvider;
        this.verificationBankLocalDataSourceProvider = verificationBankLocalDataSourceProvider;
    }

    public static ProvideVerificationBankRepositoryFactory create(
            Provider<Mapper<IdentificationDto, IdentificationWithDocument>> identificationWithDocumentMapper,
            VerificationBankDataModule verificationBankDataModule,
            Provider<VerificationBankNetworkDataSource> verificationBankNetworkDataSourceProvider,
            Provider<VerificationBankLocalDataSource> verificationBankLocalDataSourceProvider) {
        return new ProvideVerificationBankRepositoryFactory(
                identificationWithDocumentMapper,
                verificationBankNetworkDataSourceProvider,
                verificationBankDataModule,
                verificationBankLocalDataSourceProvider);
    }

    @Override
    public VerificationBankRepository get() {
        return Preconditions.checkNotNull(
                verificationBankDataModule.provideVerificationBankRepository(identificationWithDocumentMapperProvider.get(), verificationBankNetworkDataSourceProvider.get(), verificationBankLocalDataSourceProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
