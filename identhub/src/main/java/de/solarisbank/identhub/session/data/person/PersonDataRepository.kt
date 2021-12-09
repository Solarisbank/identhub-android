package de.solarisbank.identhub.session.data.person

import de.solarisbank.sdk.data.dto.PersonDataDto
import io.reactivex.Single

class PersonDataRepository(private val personDataDataSource: PersonDataDataSource) {

    fun getPersonData(identificationId: String): Single<PersonDataDto> {
        return personDataDataSource.getPersonData(identificationId)
    }

}