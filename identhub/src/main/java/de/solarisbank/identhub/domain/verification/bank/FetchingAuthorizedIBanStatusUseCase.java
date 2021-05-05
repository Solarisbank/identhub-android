package de.solarisbank.identhub.domain.verification.bank;

import java.util.concurrent.TimeUnit;

import de.solarisbank.identhub.data.Mapper;
import de.solarisbank.identhub.data.dto.IdentificationDto;
import de.solarisbank.identhub.data.entity.IdentificationWithDocument;
import de.solarisbank.identhub.domain.usecase.CompletableUseCase;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

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
                .flatMapCompletable(identification -> verificationBankRepository.save(identificationEntityMapper.from(identification)))
                .observeOn(AndroidSchedulers.mainThread());
    }
}
