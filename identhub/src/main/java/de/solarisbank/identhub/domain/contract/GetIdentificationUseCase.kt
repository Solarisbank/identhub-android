package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class GetIdentificationUseCase(
        private val contractSignRepository: ContractSignRepository
) : SingleUseCase<Unit, Identification>() {

    override fun invoke(param: Unit): Single<Identification> {
        return contractSignRepository.getIdentification()
                .observeOn(AndroidSchedulers.mainThread())
    }
}