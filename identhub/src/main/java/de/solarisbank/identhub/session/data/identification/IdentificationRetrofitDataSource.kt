package de.solarisbank.identhub.session.data.identification

import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.identhub.domain.data.dto.MobileNumberDto
import io.reactivex.Single

interface IdentificationRetrofitDataSource {

    fun getIdentification(identification_id: String): Single<IdentificationDto>

    fun getMobileNumber(): Single<MobileNumberDto>
}