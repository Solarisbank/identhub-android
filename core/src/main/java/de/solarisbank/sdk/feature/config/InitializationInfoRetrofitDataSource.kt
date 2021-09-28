package de.solarisbank.sdk.feature.config

import de.solarisbank.sdk.data.dto.InitializationInfoDto
import io.reactivex.Single

class InitializationInfoRetrofitDataSource(
    private val initializationInfoApi: InitializationInfoApi
    ) {

    fun getInfo(sessionUrl: String): Single<InitializationInfoDto> {
        return initializationInfoApi.getInfo("$sessionUrl/info")
    }

}