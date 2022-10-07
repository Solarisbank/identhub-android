package de.solarisbank.identhub.bank.domain.api;

import de.solarisbank.identhub.bank.domain.VerificationBankRepository;
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource;
import retrofit2.Retrofit;

public final class VerificationBankDataModule {
    public VerificationBankApi provideVerificationBankApi(final Retrofit retrofit) {
        return retrofit.create(VerificationBankApi.class);
    }

    public VerificationBankNetworkDataSource provideVerificationBankNetworkDataSource(VerificationBankApi verificationBankApi) {
        return new VerificationBankRetrofitDataSource(verificationBankApi);
    }

    public VerificationBankRepository provideVerificationBankRepository(
            VerificationBankNetworkDataSource verificationBankNetworkDataSource,
            IdentificationLocalDataSource identificationLocalDataSource) {
        return new VerificationBankDataSourceRepository(verificationBankNetworkDataSource, identificationLocalDataSource);
    }
}
