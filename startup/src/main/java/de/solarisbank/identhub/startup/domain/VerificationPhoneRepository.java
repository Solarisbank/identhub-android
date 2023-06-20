package de.solarisbank.identhub.startup.domain;

import de.solarisbank.identhub.startup.model.TransactionAuthenticationNumber;
import de.solarisbank.identhub.startup.model.VerificationPhoneResponse;
import de.solarisbank.identhub.startup.model.TransactionAuthenticationNumber;
import de.solarisbank.identhub.startup.model.VerificationPhoneResponse;
import io.reactivex.Single;

public interface VerificationPhoneRepository {

    Single<VerificationPhoneResponse> authorize();

    Single<VerificationPhoneResponse> confirmToken(final TransactionAuthenticationNumber token);
}
