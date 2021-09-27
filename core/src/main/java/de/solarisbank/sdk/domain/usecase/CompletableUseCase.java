package de.solarisbank.sdk.domain.usecase;

import io.reactivex.Completable;

public interface CompletableUseCase<Param> {
    Completable execute(Param param);
}
