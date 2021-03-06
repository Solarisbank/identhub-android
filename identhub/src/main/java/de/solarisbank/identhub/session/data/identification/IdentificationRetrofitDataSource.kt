package de.solarisbank.identhub.session.data.identification

import de.solarisbank.identhub.data.dto.IdentificationDto
import io.reactivex.Single

interface IdentificationRetrofitDataSource {

    fun getIdentification(identification_id: String): Single<IdentificationDto>
}