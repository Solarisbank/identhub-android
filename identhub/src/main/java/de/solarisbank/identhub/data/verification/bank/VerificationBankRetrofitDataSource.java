package de.solarisbank.identhub.data.verification.bank;

import de.solarisbank.identhub.data.verification.bank.model.IBan;
import de.solarisbank.identhub.domain.data.dto.IdentificationDto;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class VerificationBankRetrofitDataSource implements VerificationBankNetworkDataSource {

    private final VerificationBankApi verificationBankApi;

    public VerificationBankRetrofitDataSource(VerificationBankApi verificationBankApi) {
        this.verificationBankApi = verificationBankApi;
    }

    @Override
    public Single<IdentificationDto> postVerify(final IBan iBan) {
        return verificationBankApi.postVerify(iBan)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<IdentificationDto> postBankIdIdentification(final IBan iBan) {
        return verificationBankApi.postBankIdIdentification(iBan)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<IdentificationDto> getVerificationStatus(final String identificationId) {
        return verificationBankApi.getVerification(identificationId)
                .subscribeOn(Schedulers.io());
    }
}
