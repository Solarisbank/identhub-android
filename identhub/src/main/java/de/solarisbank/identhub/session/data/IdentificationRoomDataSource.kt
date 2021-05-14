package de.solarisbank.identhub.session.data

import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.Identification
import io.reactivex.Maybe

class IdentificationRoomDataSource(private val identificationDao: IdentificationDao) {

    fun getLastIdentification(): Maybe<IdentificationDto> {
        return identificationDao.getAll()
                .toMaybe()
                .flatMap { lastIdentification(it) }
                .map { IdentificationDto(id = it.id, status = it.status, url = it.url, documents = null) }
    }

    private fun lastIdentification(list: List<Identification>): Maybe<Identification> {
        return if (list.isNotEmpty()) {
            Maybe.just(list.last())
        } else {
            Maybe.empty()
        }
    }
}