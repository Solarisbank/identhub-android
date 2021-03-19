package de.solarisbank.identhub.domain.verification.phone;

import de.solarisbank.identhub.data.TransactionAuthenticationNumber;
import de.solarisbank.identhub.data.verification.phone.model.VerificationPhoneResponse;
import io.reactivex.Single;

public interface VerificationPhoneRepository {

    Single<VerificationPhoneResponse> authorize();

    Single<VerificationPhoneResponse> confirmToken(final TransactionAuthenticationNumber token);
}
