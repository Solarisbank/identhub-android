package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.identhub.session.data.person.PersonDataDataSource
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import io.reactivex.Completable
import io.reactivex.Single

class FourthlineIdentificationRepository(
    private val fourthlineIdentificationRetrofitDataSource: FourthlineIdentificationRetrofitDataSource,
    private val identificationRoomDataSource: IdentificationLocalDataSource,
    private val personDataDataSource: PersonDataDataSource
        ) {

    fun postFourthlineIdentication(): Single<IdentificationDto> {
        return fourthlineIdentificationRetrofitDataSource.postFourthlineIdentication()
    }

    fun save(identificationDto: IdentificationDto): Completable {
        return identificationRoomDataSource.saveIdentification(identificationDto)
    }

    fun getLastSavedLocalIdentification(): Single<IdentificationDto> {
        return identificationRoomDataSource.obtainIdentificationDto()
    }

    fun getPersonData(identificationId: String): Single<PersonDataDto> {
        return personDataDataSource.getPersonData(identificationId)
    }

}