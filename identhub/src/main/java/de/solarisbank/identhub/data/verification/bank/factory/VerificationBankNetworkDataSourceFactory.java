package de.solarisbank.identhub.data.verification.bank.factory;

import de.solarisbank.identhub.data.verification.bank.VerificationBankApi;
import de.solarisbank.identhub.data.verification.bank.VerificationBankModule;
import de.solarisbank.identhub.data.verification.bank.VerificationBankNetworkDataSource;
import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Preconditions;
import de.solarisbank.identhub.di.internal.Provider;

public class VerificationBankNetworkDataSourceFactory implements Factory<VerificationBankNetworkDataSource> {

    private final VerificationBankModule verificationBankModule;
    private final Provider<VerificationBankApi> verificationBankApiProvider;

    public VerificationBankNetworkDataSourceFactory(
            Provider<VerificationBankApi> verificationBankApiProvider,
            VerificationBankModule verificationBankModule) {
        this.verificationBankModule = verificationBankModule;
        this.verificationBankApiProvider = verificationBankApiProvider;
    }

    public static VerificationBankNetworkDataSourceFactory create(VerificationBankModule verificationBankModule, Provider<VerificationBankApi> verificationBankApiProvider) {
        return new VerificationBankNetworkDataSourceFactory(verificationBankApiProvider, verificationBankModule);
    }

    @Override
    public VerificationBankNetworkDataSource get() {
        return Preconditions.checkNotNull(
                verificationBankModule.provideVerificationBankNetworkDataSource(verificationBankApiProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
