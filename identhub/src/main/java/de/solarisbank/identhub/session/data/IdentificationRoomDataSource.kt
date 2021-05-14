package de.solarisbank.identhub.session.data

import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.dto.IdentificationDto
import io.reactivex.Maybe

class IdentificationRoomDataSource(private val identificationDao: IdentificationDao) {

    fun getLastIdentification(): Maybe<IdentificationDto> {
        return identificationDao.getAll()
                .toMaybe()
                .map { it.last() }
                .map { IdentificationDto(id = it.id, status = it.status, url = it.url, documents = null) }
    }
}