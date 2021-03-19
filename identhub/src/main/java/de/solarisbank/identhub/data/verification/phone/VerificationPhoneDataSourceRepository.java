package de.solarisbank.identhub.data.verification.phone;

import de.solarisbank.identhub.data.TransactionAuthenticationNumber;
import de.solarisbank.identhub.data.verification.phone.model.VerificationPhoneResponse;
import de.solarisbank.identhub.domain.verification.phone.VerificationPhoneRepository;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class VerificationPhoneDataSourceRepository implements VerificationPhoneRepository {

    private final VerificationPhoneNetworkDataSource verificationPhoneNetworkDataSource;

    public VerificationPhoneDataSourceRepository(VerificationPhoneNetworkDataSource verificationPhoneNetworkDataSource) {
        this.verificationPhoneNetworkDataSource = verificationPhoneNetworkDataSource;
    }

    @Override
    public Single<VerificationPhoneResponse> authorize() {
        return verificationPhoneNetworkDataSource.postAuthorize()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<VerificationPhoneResponse> confirmToken(TransactionAuthenticationNumber token) {
        return verificationPhoneNetworkDataSource.postConfirm(token)
                .subscribeOn(Schedulers.io());
    }
}
