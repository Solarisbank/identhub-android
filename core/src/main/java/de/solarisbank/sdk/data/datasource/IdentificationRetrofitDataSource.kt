package de.solarisbank.sdk.data.datasource

import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single

interface IdentificationRetrofitDataSource {

    fun getIdentification(identification_id: String): Single<IdentificationDto>

}