package de.solarisbank.identhub.session.data

import de.solarisbank.identhub.data.initialization.InitializeIdentificationApi
import de.solarisbank.identhub.domain.data.dto.InitializationDto
import io.reactivex.Single

class DynamicIdetityRetrofitDataSource(private val initializeIdentificationApi: InitializeIdentificationApi) {


    fun getInitialization(url: String): Single<InitializationDto> {
        return initializeIdentificationApi.getInitialization(url)
    }

}