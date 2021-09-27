package de.solarisbank.identhub.session.data.verification.phone;

import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber;
import de.solarisbank.identhub.session.data.verification.phone.model.VerificationPhoneResponse;
import io.reactivex.Single;

public class VerificationPhoneRetrofitDataSource implements VerificationPhoneNetworkDataSource {

    private final VerificationPhoneApi verificationPhoneApi;

    public VerificationPhoneRetrofitDataSource(final VerificationPhoneApi verificationPhoneApi) {
        this.verificationPhoneApi = verificationPhoneApi;
    }

    @Override
    public Single<VerificationPhoneResponse> postAuthorize() {
        return verificationPhoneApi.postAuthorize();
    }

    @Override
    public Single<VerificationPhoneResponse> postConfirm(final TransactionAuthenticationNumber tan) {
        return verificationPhoneApi.postConfirm(tan);
    }
}
