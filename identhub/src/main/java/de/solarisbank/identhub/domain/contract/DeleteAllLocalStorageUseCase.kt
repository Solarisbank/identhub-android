package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.sdk.domain.usecase.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

class DeleteAllLocalStorageUseCase(
        private val contractSignRepository: ContractSignRepository
) : CompletableUseCase<Unit> {

    override fun execute(param: Unit): Completable {
        return contractSignRepository.deleteAll()
                .observeOn(AndroidSchedulers.mainThread())
    }
}