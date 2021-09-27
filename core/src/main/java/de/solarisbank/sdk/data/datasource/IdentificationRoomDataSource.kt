package de.solarisbank.sdk.data.datasource

import de.solarisbank.sdk.data.dao.IdentificationDao
import de.solarisbank.sdk.data.entity.Identification
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