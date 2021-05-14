package de.solarisbank.identhub.session.data

import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.dto.InitializationDto
import io.reactivex.Maybe
import io.reactivex.Single

class IdentHubSessionRepository(
        private val dynamicIdetityRetrofitDataSource: DynamicIdetityRetrofitDataSource,
        private val identificationRoomDataSource: IdentificationRoomDataSource
        ) {

        fun getSavedIdentificationId(): Maybe<IdentificationDto> {
                return identificationRoomDataSource.getLastIdentification()
        }

        fun getRequiredIdentificationFlow(url: String): Single<InitializationDto> {
                return dynamicIdetityRetrofitDataSource.getInitialization(url)
        }

}