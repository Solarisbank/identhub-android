package de.solarisbank.identhub.domain.contract;

import de.solarisbank.identhub.data.TransactionAuthenticationNumber;
import de.solarisbank.identhub.data.dto.IdentificationDto;
import de.solarisbank.identhub.data.entity.Identification;
import de.solarisbank.identhub.domain.usecase.CompletableUseCase;
import io.reactivex.Completable;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class ConfirmContractSignUseCase implements CompletableUseCase<String> {
    private final ContractSignRepository contractSignRepository;

    public ConfirmContractSignUseCase(ContractSignRepository contractSignRepository) {
        this.contractSignRepository = contractSignRepository;
    }

    @Override
    public Completable execute(final String confirmToken) {
        return contractSignRepository.getIdentification()
                .flatMap((Function<Identification, SingleSource<IdentificationDto>>) identification -> contractSignRepository.confirmToken(identification.getId(), new TransactionAuthenticationNumber(confirmToken)))
                .flatMapCompletable(contractSignRepository::save)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
