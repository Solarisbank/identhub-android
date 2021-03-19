package de.solarisbank.identhub.data.verification.phone.factory;

import de.solarisbank.identhub.data.verification.phone.VerificationPhoneApi;
import de.solarisbank.identhub.data.verification.phone.VerificationPhoneModule;
import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Preconditions;
import de.solarisbank.identhub.di.internal.Provider;
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
