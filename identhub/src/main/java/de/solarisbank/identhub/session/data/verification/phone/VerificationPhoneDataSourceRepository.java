package de.solarisbank.identhub.session.data.verification.phone;

import de.solarisbank.identhub.domain.verification.phone.VerificationPhoneRepository;
import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber;
import de.solarisbank.identhub.session.data.verification.phone.model.VerificationPhoneResponse;
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
