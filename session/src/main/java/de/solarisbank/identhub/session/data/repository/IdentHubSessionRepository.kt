package de.solarisbank.identhub.session.data.repository

import de.solarisbank.identhub.session.data.datasource.DynamicIdetityRetrofitDataSource
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.InitializationDto
import io.reactivex.Single

class IdentHubSessionRepository(
    private val dynamicIdetityRetrofitDataSource: DynamicIdetityRetrofitDataSource,
    private val identificationRoomDataSource: IdentificationLocalDataSource
        ) {

        fun getSavedIdentificationId(): Single<IdentificationDto> {
                return identificationRoomDataSource.obtainIdentificationDto()
        }

        fun getRequiredIdentificationFlow(): Single<InitializationDto> {
                return dynamicIdetityRetrofitDataSource.getInitialization()
        }

}