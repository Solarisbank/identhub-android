package de.solarisbank.identhub.session.data.verification.phone.factory;

import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneApi;
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneModule;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;
import retrofit2.Retrofit;

public class ProvideVerificationPhoneApiFactory implements Factory<VerificationPhoneApi> {

    private final VerificationPhoneModule verificationPhoneModule;
    private final Provider<Retrofit> retrofitProvider;

    public ProvideVerificationPhoneApiFactory(
            Provider<Retrofit> retrofitProvider,
            VerificationPhoneModule verificationPhoneModule) {
        this.verificationPhoneModule = verificationPhoneModule;
        this.retrofitProvider = retrofitProvider;
    }

    public static ProvideVerificationPhoneApiFactory create(VerificationPhoneModule verificationPhoneModule, Provider<Retrofit> retrofitProvider) {
        return new ProvideVerificationPhoneApiFactory(retrofitProvider, verificationPhoneModule);
    }

    @Override
    public VerificationPhoneApi get() {
        return Preconditions.checkNotNull(
                verificationPhoneModule.provideVerificationPhoneApi(retrofitProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
