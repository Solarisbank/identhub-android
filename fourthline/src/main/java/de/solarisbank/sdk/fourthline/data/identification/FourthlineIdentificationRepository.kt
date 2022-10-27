package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.person.PersonDataSource
import io.reactivex.Completable
import io.reactivex.Single

class FourthlineIdentificationRepository(
    private val fourthlineIdentificationDataSource: FourthlineIdentificationDataSource,
    private val identificationLocalDataSource: IdentificationLocalDataSource,
    private val personDataDataSource: PersonDataSource
        ) {

    fun postFourthlineSimplifiedIdentication(): Single<IdentificationDto> {
        return fourthlineIdentificationDataSource.postFourthlineIdentification()
    }

    fun postFourthlineSigningIdentication(): Single<IdentificationDto> {
        return fourthlineIdentificationDataSource.postFourthlineSigningIdentification()
    }

    fun save(identificationDto: IdentificationDto): Completable {
        return identificationLocalDataSource.saveIdentification(identificationDto)
    }

    fun getLastSavedLocalIdentification(): Single<IdentificationDto> {
        return identificationLocalDataSource.obtainIdentificationDto()
    }

    fun getPersonData(identificationId: String): Single<PersonDataDto> {
        return personDataDataSource.getPersonData(identificationId)
    }

}