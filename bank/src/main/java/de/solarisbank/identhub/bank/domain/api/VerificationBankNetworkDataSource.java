package de.solarisbank.identhub.bank.domain.api;

import de.solarisbank.identhub.bank.data.Iban;
import de.solarisbank.sdk.data.dto.IdentificationDto;
import io.reactivex.Single;

public interface VerificationBankNetworkDataSource {
    Single<IdentificationDto> postVerify(final Iban iBan);

    Single<IdentificationDto> postBankIdIdentification(final Iban iBan);

    Single<IdentificationDto> getVerificationStatus(final String identificationId);
}
