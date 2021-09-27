package de.solarisbank.identhub.session.data.verification.bank.factory;

import de.solarisbank.identhub.session.data.verification.bank.VerificationBankApi;
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankDataModule;
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankNetworkDataSource;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

public class VerificationBankNetworkDataSourceFactory implements Factory<VerificationBankNetworkDataSource> {

    private final VerificationBankDataModule verificationBankDataModule;
    private final Provider<VerificationBankApi> verificationBankApiProvider;

    public VerificationBankNetworkDataSourceFactory(
            Provider<VerificationBankApi> verificationBankApiProvider,
            VerificationBankDataModule verificationBankDataModule) {
        this.verificationBankDataModule = verificationBankDataModule;
        this.verificationBankApiProvider = verificationBankApiProvider;
    }

    public static VerificationBankNetworkDataSourceFactory create(VerificationBankDataModule verificationBankDataModule, Provider<VerificationBankApi> verificationBankApiProvider) {
        return new VerificationBankNetworkDataSourceFactory(verificationBankApiProvider, verificationBankDataModule);
    }

    @Override
    public VerificationBankNetworkDataSource get() {
        return Preconditions.checkNotNull(
                verificationBankDataModule.provideVerificationBankNetworkDataSource(verificationBankApiProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
