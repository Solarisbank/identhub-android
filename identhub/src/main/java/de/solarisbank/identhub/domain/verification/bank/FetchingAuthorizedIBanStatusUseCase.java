package de.solarisbank.identhub.domain.verification.bank;

import java.util.concurrent.TimeUnit;

import de.solarisbank.identhub.session.data.Mapper;
import de.solarisbank.sdk.data.dto.IdentificationDto;
import de.solarisbank.sdk.data.entity.IdentificationWithDocument;
import de.solarisbank.sdk.domain.usecase.CompletableUseCase;
import io.reactivex.Completable;

public class FetchingAuthorizedIBanStatusUseCase implements CompletableUseCase<String> {
    private final VerificationBankRepository verificationBankRepository;
    private final Mapper<IdentificationDto, IdentificationWithDocument> identificationEntityMapper;

    public FetchingAuthorizedIBanStatusUseCase(Mapper<IdentificationDto, IdentificationWithDocument> identificationEntityMapper,
                                               VerificationBankRepository verificationBankRepository) {
        this.verificationBankRepository = verificationBankRepository;
        this.identificationEntityMapper = identificationEntityMapper;
    }

    @Override
    public Completable execute(String identificationId) {
        return verificationBankRepository.getVerificationStatus(identificationId)
                .repeatWhen(flowable -> flowable.delay(3, TimeUnit.SECONDS))
                .map(identificationEntityMapper::to)
                .takeUntil(identificationWithDocument -> {
                    return identificationWithDocument.getIdentification().isAuthorizationRequiredOrFailed();
                })
                .filter(identificationWithDocument -> identificationWithDocument.getIdentification().isAuthorizationRequiredOrFailed())
                .flatMapCompletable(identification -> verificationBankRepository.save(identificationEntityMapper.from(identification)));
    }
}
