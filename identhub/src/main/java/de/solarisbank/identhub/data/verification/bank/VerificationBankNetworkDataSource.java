package de.solarisbank.identhub.data.verification.bank;

import de.solarisbank.identhub.data.verification.bank.model.IBan;
import de.solarisbank.identhub.domain.data.dto.IdentificationDto;
import io.reactivex.Single;

public interface VerificationBankNetworkDataSource {
    Single<IdentificationDto> postVerify(final IBan iBan);

    Single<IdentificationDto> postBankIdIdentification(final IBan iBan);

    Single<IdentificationDto> getVerificationStatus(final String identificationId);
}
