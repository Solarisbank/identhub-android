package de.solarisbank.identhub.session.data.verification.phone;

import de.solarisbank.identhub.domain.verification.phone.VerificationPhoneRepository;
import retrofit2.Retrofit;

public class VerificationPhoneModule {
    public VerificationPhoneApi provideVerificationPhoneApi(final Retrofit retrofit) {
        return retrofit.create(VerificationPhoneApi.class);
    }

    public VerificationPhoneNetworkDataSource provideVerificationPhoneNetworkDataSource(VerificationPhoneApi verificationPhoneApi) {
        return new VerificationPhoneRetrofitDataSource(verificationPhoneApi);
    }

    public VerificationPhoneRepository provideVerificationPhoneRepository(VerificationPhoneNetworkDataSource verificationPhoneNetworkDataSource) {
        return new VerificationPhoneDataSourceRepository(verificationPhoneNetworkDataSource);
    }
}
