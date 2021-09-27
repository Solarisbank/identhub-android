package de.solarisbank.identhub.session.data.contract

import de.solarisbank.sdk.data.dao.DocumentDao
import de.solarisbank.sdk.data.dao.IdentificationDao
import de.solarisbank.sdk.data.entity.Document
import de.solarisbank.sdk.data.entity.Identification
import de.solarisbank.sdk.data.entity.IdentificationWithDocument
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ContractSignRoomDataSource(
        private val documentDao: DocumentDao,
        private val identificationDao: IdentificationDao
) : ContractSignLocalDataSource {

    override fun getAllDocuments(): Single<List<Document>> {
        return documentDao.getAll().subscribeOn(Schedulers.io())
    }

    override fun deleteAll(): Completable {
        return documentDao.deleteAll()
                .andThen(identificationDao.deleteAll()).subscribeOn(Schedulers.io())
    }

    override fun getIdentification(): Single<Identification> {
        return identificationDao.get().subscribeOn(Schedulers.io())
    }

    override fun insert(identificationWithDocument: IdentificationWithDocument): Completable {
        identificationWithDocument.documents.forEach { it.identificationId = identificationWithDocument.identification.id }

        return identificationDao.insert(identificationWithDocument.identification)
                .andThen(documentDao.insert(identificationWithDocument.documents))
                .subscribeOn(Schedulers.io())
    }
}