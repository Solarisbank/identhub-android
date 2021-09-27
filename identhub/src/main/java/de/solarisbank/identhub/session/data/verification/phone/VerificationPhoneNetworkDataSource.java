package de.solarisbank.identhub.session.data.verification.phone;

import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber;
import de.solarisbank.identhub.session.data.verification.phone.model.VerificationPhoneResponse;
import io.reactivex.Single;

public interface VerificationPhoneNetworkDataSource {
    Single<VerificationPhoneResponse> postAuthorize();

    Single<VerificationPhoneResponse> postConfirm(final TransactionAuthenticationNumber tan);
}
