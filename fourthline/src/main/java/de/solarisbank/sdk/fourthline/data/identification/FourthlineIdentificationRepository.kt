package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.identhub.session.data.person.PersonDataDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRoomDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.Identification
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import io.reactivex.Completable
import io.reactivex.Single

class FourthlineIdentificationRepository(
    private val fourthlineIdentificationRetrofitDataSource: FourthlineIdentificationRetrofitDataSource,
    private val identificationRoomDataSource: IdentificationRoomDataSource,
    private val personDataDataSource: PersonDataDataSource
        ) {

    fun postFourthlineIdentication(): Single<IdentificationDto> {
        return fourthlineIdentificationRetrofitDataSource.postFourthlineIdentication()
    }

    fun save(identificationDTO: IdentificationDto): Completable {
        //todo replace with mapper
//        identificationDTO.url?.let {}
        val identification = Identification(id = identificationDTO.id, url = "", status = identificationDTO.status)
        return identificationRoomDataSource.insert(identification)
    }

    fun getLastSavedLocalIdentification(): Single<Identification> {
        return identificationRoomDataSource.getIdentification()
    }

    fun getPersonData(identificationId: String): Single<PersonDataDto> {
        return personDataDataSource.getPersonData(identificationId)
    }

}