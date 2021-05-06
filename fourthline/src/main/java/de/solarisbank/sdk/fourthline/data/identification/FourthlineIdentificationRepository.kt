package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import io.reactivex.Completable
import io.reactivex.Single

class FourthlineIdentificationRepository(
        private val fourthlineIdentificationRetrofitDataSource: FourthlineIdentificationRetrofitDataSource,
        private val fourthlineIdentificationRoomDataSource: FourthlineIdentificationRoomDataSource
        ) {

    fun postFourthlineIdentication(): Single<IdentificationDto> {
        return fourthlineIdentificationRetrofitDataSource.postFourthlineIdentication()
    }

    fun save(identificationDTO: IdentificationDto): Completable {
        //todo replace with mapper
//        identificationDTO.url?.let {}
        val identification = Identification(id = identificationDTO.id, url = "", status = identificationDTO.status)
        return fourthlineIdentificationRoomDataSource.insert(identification)
    }

    fun getLastSavedLocalIdentification(): Single<IdentificationDto> {
        return fourthlineIdentificationRoomDataSource.getLastIdentification()
    }

    fun getPersonData(identificationId: String): Single<PersonDataDto> {
        return fourthlineIdentificationRetrofitDataSource.getPersonData(identificationId)
    }

}