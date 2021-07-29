package de.solarisbank.identhub.session.data.identification

import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import io.reactivex.Single

class IdentificationRetrofitDataSourceImpl(private val identificationApi: IdentificationApi) : IdentificationRetrofitDataSource {

    override fun getIdentification(identification_id: String): Single<IdentificationDto> {
        return identificationApi.getIdentification(identification_id)
    }

}