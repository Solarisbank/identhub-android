package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class GetDocumentsUseCase(
        private val contractSignRepository: ContractSignRepository
) : SingleUseCase<Unit, List<Document>>() {

    override fun invoke(param: Unit): Single<List<Document>> {
        return contractSignRepository.getDocuments().observeOn(AndroidSchedulers.mainThread())
    }
}