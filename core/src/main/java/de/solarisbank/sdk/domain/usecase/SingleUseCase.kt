package de.solarisbank.sdk.domain.usecase

import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.model.result.Result
import io.reactivex.Single

abstract class SingleUseCase<P, R> {
    open fun execute(param: P): Single<Result<R>> {
        return invoke(param)
                .transformResult()
    }

    protected abstract fun invoke(param: P): Single<NavigationalResult<R>>
}