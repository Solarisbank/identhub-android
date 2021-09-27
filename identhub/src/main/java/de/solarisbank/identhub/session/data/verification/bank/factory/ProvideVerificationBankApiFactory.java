package de.solarisbank.identhub.session.data.verification.bank.factory;

import de.solarisbank.identhub.session.data.verification.bank.VerificationBankApi;
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankDataModule;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;
import retrofit2.Retrofit;

public class ProvideVerificationBankApiFactory implements Factory<VerificationBankApi> {

    private final VerificationBankDataModule verificationBankDataModule;
    private final Provider<Retrofit> retrofitProvider;

    public ProvideVerificationBankApiFactory(
            Provider<Retrofit> retrofitProvider,
            VerificationBankDataModule verificationBankDataModule) {
        this.verificationBankDataModule = verificationBankDataModule;
        this.retrofitProvider = retrofitProvider;
    }

    public static ProvideVerificationBankApiFactory create(VerificationBankDataModule verificationBankDataModule, Provider<Retrofit> retrofitProvider) {
        return new ProvideVerificationBankApiFactory(retrofitProvider, verificationBankDataModule);
    }

    @Override
    public VerificationBankApi get() {
        return Preconditions.checkNotNull(
                verificationBankDataModule.provideVerificationBankApi(retrofitProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
