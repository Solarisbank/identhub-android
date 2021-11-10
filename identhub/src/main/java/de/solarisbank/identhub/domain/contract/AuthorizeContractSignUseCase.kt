package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.sdk.domain.usecase.CompletableUseCase
import io.reactivex.Completable

class AuthorizeContractSignUseCase(
    private val contractSignRepository: ContractSignRepository
    ) : CompletableUseCase<Unit> {
    override fun execute(param: Unit): Completable {
        return contractSignRepository.getIdentification()
            .flatMap { (id) -> contractSignRepository.authorize(id) }
            .ignoreElement()
    }
}