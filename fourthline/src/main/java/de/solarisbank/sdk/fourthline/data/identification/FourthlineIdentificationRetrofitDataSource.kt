package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import io.reactivex.Single

class FourthlineIdentificationRetrofitDataSource(
        private val fourthlineIdentificationApi: FourthlineIdentificationApi
        ) {

    fun postFourthlineIdentication(): Single<IdentificationDto> {
        return fourthlineIdentificationApi.postFourthlineIdentication()
    }

    fun getPersonData(identificationId: String): Single<PersonDataDto> {
        return fourthlineIdentificationApi.getPersonData(identificationId)
    }

    fun getIdentification(identificationId: String): Single<IdentificationDto> {
        return fourthlineIdentificationApi.getIdentifications(identificationId)
    }


}