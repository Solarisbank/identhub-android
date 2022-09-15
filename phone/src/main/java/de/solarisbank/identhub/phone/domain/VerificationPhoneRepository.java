package de.solarisbank.identhub.phone.domain;

import de.solarisbank.identhub.phone.model.TransactionAuthenticationNumber;
import de.solarisbank.identhub.phone.model.VerificationPhoneResponse;
import io.reactivex.Single;

public interface VerificationPhoneRepository {

    Single<VerificationPhoneResponse> authorize();

    Single<VerificationPhoneResponse> confirmToken(final TransactionAuthenticationNumber token);
}
