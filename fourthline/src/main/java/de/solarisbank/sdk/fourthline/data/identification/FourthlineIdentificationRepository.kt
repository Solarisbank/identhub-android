package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.identhub.session.data.person.PersonDataDataSource
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.PersonDataDto
import io.reactivex.Completable
import io.reactivex.Single

class FourthlineIdentificationRepository(
    private val fourthlineIdentificationRetrofitDataSource: FourthlineIdentificationRetrofitDataSource,
    private val identificationLocalDataSource: IdentificationLocalDataSource,
    private val personDataDataSource: PersonDataDataSource
        ) {

    fun postFourthlineSimplifiedIdentication(): Single<IdentificationDto> {
        return fourthlineIdentificationRetrofitDataSource.postFourthlineIdentication()
    }

    fun postFourthlineSigningIdentication(): Single<IdentificationDto> {
        return fourthlineIdentificationRetrofitDataSource.postFourthlineSigningIdentication()
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