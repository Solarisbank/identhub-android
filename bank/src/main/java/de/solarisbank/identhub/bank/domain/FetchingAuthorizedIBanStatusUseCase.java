package de.solarisbank.identhub.bank.domain;

import java.util.concurrent.TimeUnit;

import de.solarisbank.sdk.data.dto.IdentificationDto;
import de.solarisbank.sdk.data.entity.Status;
import de.solarisbank.sdk.domain.usecase.CompletableUseCase;
import io.reactivex.Completable;

public class FetchingAuthorizedIBanStatusUseCase implements CompletableUseCase<String> {
    private final VerificationBankRepository verificationBankRepository;

    public FetchingAuthorizedIBanStatusUseCase(VerificationBankRepository verificationBankRepository) {
        this.verificationBankRepository = verificationBankRepository;
    }

    @Override
    public Completable execute(String identificationId) {
        return verificationBankRepository.getVerificationStatus(identificationId)
                .repeatWhen(flowable -> flowable.delay(3, TimeUnit.SECONDS))
                .takeUntil(identificationDto -> {
                    return isAuthorizationRequiredOrFailed(identificationDto);
                })
                .filter(identificationDto -> isAuthorizationRequiredOrFailed(identificationDto))
                .flatMapCompletable(identification -> verificationBankRepository.save(identification));
    }

    private boolean isAuthorizationRequiredOrFailed(IdentificationDto identificationDto) {
        Status currentStatus = Status.Companion.getEnum(identificationDto.getStatus());
        return Status.AUTHORIZATION_REQUIRED == currentStatus
                || Status.IDENTIFICATION_DATA_REQUIRED == currentStatus
                || Status.FAILED == currentStatus;
    }

}
