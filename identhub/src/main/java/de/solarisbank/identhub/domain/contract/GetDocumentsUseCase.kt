package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import io.reactivex.Single

class GetDocumentsUseCase(
        private val contractSignRepository: ContractSignRepository
) : SingleUseCase<Unit, List<Document>>() {

    override fun invoke(param: Unit): Single<NavigationalResult<List<Document>>> {
        return contractSignRepository.getDocuments()
                .map { NavigationalResult(it) }
    }
}