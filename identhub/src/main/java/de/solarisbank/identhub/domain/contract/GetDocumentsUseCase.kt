package de.solarisbank.identhub.domain.contract

import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single

class GetDocumentsUseCase(
        private val contractSignRepository: ContractSignRepository
) : SingleUseCase<Unit, List<DocumentDto>>() {

    override fun invoke(param: Unit): Single<NavigationalResult<List<DocumentDto>>> {
        return contractSignRepository.getIdentification()
                .map { NavigationalResult(if (it.documents != null) it.documents!! else listOf()) }
    }
}