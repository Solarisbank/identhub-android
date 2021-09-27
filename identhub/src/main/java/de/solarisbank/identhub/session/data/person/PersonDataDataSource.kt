package de.solarisbank.identhub.session.data.person

import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import io.reactivex.Single

class PersonDataDataSource(private val personDataApi: PersonDataApi) {

    fun getPersonData(identificationId: String): Single<PersonDataDto> {
        return personDataApi.getPersonData(identificationId)
    }

}