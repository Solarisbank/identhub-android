package de.solarisbank.identhub.session.data.verification.bank.factory;

import de.solarisbank.identhub.domain.verification.bank.VerificationBankRepository;
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankDataModule;
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankNetworkDataSource;
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

public class ProvideVerificationBankRepositoryFactory implements Factory<VerificationBankRepository> {

    private final VerificationBankDataModule verificationBankDataModule;
    private final Provider<VerificationBankNetworkDataSource> verificationBankNetworkDataSourceProvider;
    private final Provider<? extends IdentificationLocalDataSource> identificationLocalDataSourceProvider;

    public ProvideVerificationBankRepositoryFactory(
            Provider<VerificationBankNetworkDataSource> verificationBankNetworkDataSourceProvider,
            VerificationBankDataModule verificationBankDataModule,
            Provider<? extends IdentificationLocalDataSource> identificationLocalDataSourceProvider) {
        this.verificationBankDataModule = verificationBankDataModule;
        this.verificationBankNetworkDataSourceProvider = verificationBankNetworkDataSourceProvider;
        this.identificationLocalDataSourceProvider = identificationLocalDataSourceProvider;
    }

    public static ProvideVerificationBankRepositoryFactory create(
            VerificationBankDataModule verificationBankDataModule,
            Provider<VerificationBankNetworkDataSource> verificationBankNetworkDataSourceProvider,
            Provider<? extends IdentificationLocalDataSource> identificationLocalDataSourceProvider) {
        return new ProvideVerificationBankRepositoryFactory(
                verificationBankNetworkDataSourceProvider,
                verificationBankDataModule,
                identificationLocalDataSourceProvider);
    }

    @Override
    public VerificationBankRepository get() {
        return Preconditions.checkNotNull(
                verificationBankDataModule.provideVerificationBankRepository(
                        verificationBankNetworkDataSourceProvider.get(), identificationLocalDataSourceProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
