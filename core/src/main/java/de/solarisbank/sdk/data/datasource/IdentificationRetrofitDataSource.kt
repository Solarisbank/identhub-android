package de.solarisbank.sdk.data.datasource

import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single

class IdentificationRetrofitDataSource(private val identificationApi: IdentificationApi) :
    IdentificationRemoteDataSource {

    override fun getIdentification(identificationId: String): Single<IdentificationDto> {
        return identificationApi.getIdentification(identificationId)
    }

}