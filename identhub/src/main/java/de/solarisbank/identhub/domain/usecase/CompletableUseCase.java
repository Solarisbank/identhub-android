package de.solarisbank.identhub.domain.usecase;

import io.reactivex.Completable;

public interface CompletableUseCase<Param> {
    Completable execute(Param param);
}
