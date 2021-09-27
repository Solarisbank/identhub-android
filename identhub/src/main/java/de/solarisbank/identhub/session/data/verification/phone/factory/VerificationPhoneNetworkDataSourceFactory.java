package de.solarisbank.identhub.session.data.verification.phone.factory;

import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneApi;
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneModule;
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneNetworkDataSource;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

public class VerificationPhoneNetworkDataSourceFactory implements Factory<VerificationPhoneNetworkDataSource> {

    private final VerificationPhoneModule verificationPhoneModule;
    private final Provider<VerificationPhoneApi> verificationPhoneApiProvider;

    public VerificationPhoneNetworkDataSourceFactory(
            Provider<VerificationPhoneApi> verificationPhoneApiProvider,
            VerificationPhoneModule verificationPhoneModule) {
        this.verificationPhoneModule = verificationPhoneModule;
        this.verificationPhoneApiProvider = verificationPhoneApiProvider;
    }

    public static VerificationPhoneNetworkDataSourceFactory create(VerificationPhoneModule verificationPhoneModule, Provider<VerificationPhoneApi> verificationPhoneApiProvider) {
        return new VerificationPhoneNetworkDataSourceFactory(verificationPhoneApiProvider, verificationPhoneModule);
    }

    @Override
    public VerificationPhoneNetworkDataSource get() {
        return Preconditions.checkNotNull(
                verificationPhoneModule.provideVerificationPhoneNetworkDataSource(verificationPhoneApiProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
