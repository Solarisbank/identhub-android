package de.solarisbank.identhub.data.verification.bank.factory;

import de.solarisbank.identhub.data.verification.bank.VerificationBankApi;
import de.solarisbank.identhub.data.verification.bank.VerificationBankModule;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.di.internal.Provider;
import retrofit2.Retrofit;

public class ProvideVerificationBankApiFactory implements Factory<VerificationBankApi> {

    private final VerificationBankModule verificationBankModule;
    private final Provider<Retrofit> retrofitProvider;

    public ProvideVerificationBankApiFactory(
            Provider<Retrofit> retrofitProvider,
            VerificationBankModule verificationBankModule) {
        this.verificationBankModule = verificationBankModule;
        this.retrofitProvider = retrofitProvider;
    }

    public static ProvideVerificationBankApiFactory create(VerificationBankModule verificationBankModule, Provider<Retrofit> retrofitProvider) {
        return new ProvideVerificationBankApiFactory(retrofitProvider, verificationBankModule);
    }

    @Override
    public VerificationBankApi get() {
        return Preconditions.checkNotNull(
                verificationBankModule.provideVerificationBankApi(retrofitProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
