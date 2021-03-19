package de.solarisbank.identhub.domain.contract;

import de.solarisbank.identhub.domain.usecase.CompletableUseCase;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;

public class AuthorizeContractSignUseCase implements CompletableUseCase<Unit> {
    private final ContractSignRepository contractSignRepository;

    public AuthorizeContractSignUseCase(ContractSignRepository contractSignRepository) {
        this.contractSignRepository = contractSignRepository;
    }

    @Override
    public Completable execute(Unit param) {
        return contractSignRepository.getIdentification()
                .flatMap(identification -> contractSignRepository.authorize(identification.getId()))
                .ignoreElement()
                .observeOn(AndroidSchedulers.mainThread());
    }
}
