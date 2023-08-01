package de.solarisbank.sdk.fourthline.data.person

import de.solarisbank.sdk.data.dto.PersonDataDto

interface PersonDataSource {
    suspend fun getPersonData(identificationId: String, getRawDocumentList: Boolean): PersonDataDto
}

class PersonDataSourceImpl(private val personDataApi: PersonDataApi): PersonDataSource {

    override suspend fun getPersonData(identificationId: String, getRawDocumentList: Boolean): PersonDataDto {
        return personDataApi.getPersonData(identificationId, getRawDocumentList)
    }
}