package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.Identification
import io.reactivex.Completable
import io.reactivex.Single

class FourthlineIdentificationRoomDataSource(private val identificationDao: IdentificationDao) {

    fun insert(identification: Identification): Completable {
        return identificationDao.insert(identification)
    }

    fun getLastIdentification(): Single<IdentificationDto> {
        return identificationDao.getAll()
                .map { it.last() }
                .map { IdentificationDto(id = it.id, status = it.status, url = it.url, documents = null) }
    }

}