package de.solarisbank.identhub.session.data.verification.phone.factory;

import de.solarisbank.identhub.domain.verification.phone.VerificationPhoneRepository;
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneModule;
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneNetworkDataSource;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

public class ProvideVerificationPhoneRepositoryFactory implements Factory<VerificationPhoneRepository> {

    private final VerificationPhoneModule verificationPhoneModule;
    private final Provider<VerificationPhoneNetworkDataSource> verificationPhoneNetworkDataSourceProvider;

    public ProvideVerificationPhoneRepositoryFactory(
            Provider<VerificationPhoneNetworkDataSource> verificationPhoneNetworkDataSourceProvider,
            VerificationPhoneModule verificationPhoneModule) {
        this.verificationPhoneModule = verificationPhoneModule;
        this.verificationPhoneNetworkDataSourceProvider = verificationPhoneNetworkDataSourceProvider;
    }

    public static ProvideVerificationPhoneRepositoryFactory create(VerificationPhoneModule verificationPhoneModule, Provider<VerificationPhoneNetworkDataSource> verificationPhoneNetworkDataSourceProvider) {
        return new ProvideVerificationPhoneRepositoryFactory(verificationPhoneNetworkDataSourceProvider, verificationPhoneModule);
    }

    @Override
    public VerificationPhoneRepository get() {
        return Preconditions.checkNotNull(
                verificationPhoneModule.provideVerificationPhoneRepository(verificationPhoneNetworkDataSourceProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
