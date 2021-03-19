package de.solarisbank.identhub.data.verification.bank;

import de.solarisbank.identhub.data.dto.IdentificationDto;
import de.solarisbank.identhub.data.verification.bank.model.IBan;
import io.reactivex.Single;

public interface VerificationBankNetworkDataSource {
    Single<IdentificationDto> postVerify(final IBan iBan);

    Single<IdentificationDto> getVerificationStatus(final String identificationId);
}
