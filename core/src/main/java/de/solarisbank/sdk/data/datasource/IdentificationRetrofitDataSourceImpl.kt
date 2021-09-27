package de.solarisbank.sdk.data.datasource

import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single

class IdentificationRetrofitDataSourceImpl(private val identificationApi: IdentificationApi) :
    IdentificationRetrofitDataSource {

    override fun getIdentification(identification_id: String): Single<IdentificationDto> {
        return identificationApi.getIdentification(identification_id)
    }

}