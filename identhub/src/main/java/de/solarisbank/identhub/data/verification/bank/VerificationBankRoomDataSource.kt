package de.solarisbank.identhub.data.verification.bank

import de.solarisbank.identhub.data.dao.DocumentDao
import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.data.entity.IdentificationWithDocument
import io.reactivex.Completable
import io.reactivex.Single

class VerificationBankRoomDataSource(
        private val documentDao: DocumentDao,
        private val identificationDao: IdentificationDao
) : VerificationBankLocalDataSource {
    override fun getIdentificationDto(): Single<Identification> {
        return identificationDao.get()
    }

    override fun insert(identificationWithDocument: IdentificationWithDocument): Completable {
        identificationWithDocument.documents.forEach { it.identificationId = identificationWithDocument.identification.id }

        return identificationDao.insert(identificationWithDocument.identification)
                .andThen(documentDao.insert(identificationWithDocument.documents))
    }

    override fun deleteALl(): Completable {
        return documentDao.deleteAll()
                .andThen(identificationDao.deleteAll())
    }

}