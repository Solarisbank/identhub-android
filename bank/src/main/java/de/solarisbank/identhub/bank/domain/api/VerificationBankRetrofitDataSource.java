package de.solarisbank.identhub.bank.domain.api;

import de.solarisbank.identhub.bank.data.Iban;
import de.solarisbank.sdk.data.dto.IdentificationDto;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class VerificationBankRetrofitDataSource implements VerificationBankNetworkDataSource {

    private final VerificationBankApi verificationBankApi;

    public VerificationBankRetrofitDataSource(VerificationBankApi verificationBankApi) {
        this.verificationBankApi = verificationBankApi;
    }

    @Override
    public Single<IdentificationDto> postVerify(final Iban iBan) {
        return verificationBankApi.postVerify(iBan)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<IdentificationDto> postBankIdIdentification(final Iban iBan) {
        return verificationBankApi.postBankIdIdentification(iBan)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<IdentificationDto> getVerificationStatus(final String identificationId) {
        return verificationBankApi.getVerification(identificationId)
                .subscribeOn(Schedulers.io());
    }
}
