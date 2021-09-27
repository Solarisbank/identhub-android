package de.solarisbank.identhub.session.data.datasource

import de.solarisbank.identhub.session.data.network.InitializeIdentificationApi
import de.solarisbank.sdk.data.dto.InitializationDto
import io.reactivex.Single

class DynamicIdetityRetrofitDataSource(
    private val initializeIdentificationApi: InitializeIdentificationApi
    ) {

    fun getInitialization(url: String): Single<InitializationDto> {
        return initializeIdentificationApi.getInitialization(url)
    }

}