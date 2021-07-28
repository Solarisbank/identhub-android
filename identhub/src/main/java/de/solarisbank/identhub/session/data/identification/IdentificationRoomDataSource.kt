package de.solarisbank.identhub.session.data.identification

import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.entity.Identification
import io.reactivex.Completable
import io.reactivex.Single

class IdentificationRoomDataSource(private val identificationDao: IdentificationDao) {

    fun getIdentification(): Single<Identification> {
        return identificationDao.get()
    }

    fun insert(identification: Identification): Completable {
        return identificationDao.deleteAll()
                .andThen(identificationDao.insert(identification))
    }

}