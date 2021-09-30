package de.solarisbank.identhub.domain.contract;

import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber;
import de.solarisbank.sdk.data.dto.IdentificationDto;
import de.solarisbank.sdk.domain.usecase.CompletableUseCase;
import io.reactivex.Completable;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class ConfirmContractSignUseCase implements CompletableUseCase<String> {
    private final ContractSignRepository contractSignRepository;

    public ConfirmContractSignUseCase(ContractSignRepository contractSignRepository) {
        this.contractSignRepository = contractSignRepository;
    }

    @Override
    public Completable execute(final String confirmToken) {
        return contractSignRepository.getIdentification()
                .flatMap(
                        (Function<IdentificationDto, SingleSource<IdentificationDto>>) identification ->
                        {
                            Timber.d("contractSignRepository.getIdentification(), identification: " + identification);
                            return contractSignRepository.confirmToken(identification.getId(), new TransactionAuthenticationNumber(confirmToken));
                        }
                )
                .flatMapCompletable(contractSignRepository::save);
    }
}
