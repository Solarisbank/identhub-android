package de.solarisbank.identhub.session.data.datasource

import de.solarisbank.identhub.session.data.network.InitializeIdentificationApi
import de.solarisbank.sdk.data.dto.InitializationDto
import io.reactivex.Single

class DynamicIdetityRetrofitDataSource(
    private val initializeIdentificationApi: InitializeIdentificationApi
    ) {

    fun getInitialization(): Single<InitializationDto> {
        return initializeIdentificationApi.getInitialization()
    }

}