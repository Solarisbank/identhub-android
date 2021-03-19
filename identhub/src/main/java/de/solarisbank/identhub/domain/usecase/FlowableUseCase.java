package de.solarisbank.identhub.domain.usecase;

import io.reactivex.Flowable;

public interface FlowableUseCase<Param, Result> {
    Flowable<Result> execute(Param param);
}
