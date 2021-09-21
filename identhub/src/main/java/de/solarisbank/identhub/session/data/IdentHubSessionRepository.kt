package de.solarisbank.identhub.session.data

import de.solarisbank.identhub.domain.data.dto.InitializationDto
import de.solarisbank.identhub.session.data.identification.IdentificationRoomDataSource
import de.solarisbank.sdk.data.entity.Identification
import io.reactivex.Single

class IdentHubSessionRepository(
        private val dynamicIdetityRetrofitDataSource: DynamicIdetityRetrofitDataSource,
        private val identificationRoomDataSource: IdentificationRoomDataSource
        ) {

        fun getSavedIdentificationId(): Single<Identification> {
                return identificationRoomDataSource.getIdentification()
        }

        fun getRequiredIdentificationFlow(url: String): Single<InitializationDto> {
                return dynamicIdetityRetrofitDataSource.getInitialization(url)
        }

}