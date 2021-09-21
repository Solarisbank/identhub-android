package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.sdk.data.entity.Document
import de.solarisbank.sdk.data.entity.NavigationalResult
import io.reactivex.Single

class GetDocumentsUseCase(
        private val contractSignRepository: ContractSignRepository
) : SingleUseCase<Unit, List<Document>>() {

    override fun invoke(param: Unit): Single<NavigationalResult<List<Document>>> {
        return contractSignRepository.getDocuments()
                .map { NavigationalResult(it) }
    }
}