package de.solarisbank.sdk.fourthline.data.person

import de.solarisbank.sdk.data.dto.PersonDataDto
import io.reactivex.Single

interface PersonDataSource {
    fun getPersonData(identificationId: String): Single<PersonDataDto>
}

class PersonDataSourceImpl(private val personDataApi: PersonDataApi): PersonDataSource {

    override fun getPersonData(identificationId: String): Single<PersonDataDto> {
        return personDataApi.getPersonData(identificationId)
    }

}