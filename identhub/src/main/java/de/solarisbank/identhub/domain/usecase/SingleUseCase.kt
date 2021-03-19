package de.solarisbank.identhub.domain.usecase

import de.solarisbank.identhub.data.network.transformResult
import de.solarisbank.shared.result.Result
import io.reactivex.Single

abstract class SingleUseCase<P, R> {
    open fun execute(param: P): Single<Result<R>> {
        return invoke(param)
                .transformResult()
    }

    protected abstract fun invoke(param: P): Single<R>
}