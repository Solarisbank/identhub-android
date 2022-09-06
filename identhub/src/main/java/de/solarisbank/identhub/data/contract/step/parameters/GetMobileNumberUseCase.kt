package de.solarisbank.identhub.data.contract.step.parameters


import de.solarisbank.sdk.data.dto.MobileNumberDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single

class GetMobileNumberUseCase(
    private val identificationRepository: IdentificationRepository
    ): SingleUseCase<Unit, MobileNumberDto>() {
    override fun invoke(param: Unit): Single<NavigationalResult<MobileNumberDto>> {
        return identificationRepository
            .getMobileNumber()
            .map { NavigationalResult(it) }
    }
}