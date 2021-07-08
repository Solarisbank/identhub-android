package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.dto.MobileNumberDto
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.identhub.session.data.identification.IdentificationRepository
import io.reactivex.Single

class GetMobileNumberUseCase(private val identificationRepository: IdentificationRepository): SingleUseCase<Unit, MobileNumberDto>() {
    override fun invoke(param: Unit): Single<NavigationalResult<MobileNumberDto>> {
        return identificationRepository.getMobileNumber().map { NavigationalResult<MobileNumberDto>(it) }
    }
}