package de.solarisbank.identhub.data.verification.bank

import de.solarisbank.identhub.data.dao.DocumentDao
import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.entity.IdentificationWithDocument
import io.reactivex.Completable

class VerificationBankRoomDataSource(
        private val documentDao: DocumentDao,
        private val identificationDao: IdentificationDao
) : VerificationBankLocalDataSource {

    override fun insert(identificationWithDocument: IdentificationWithDocument): Completable {
        identificationWithDocument.documents.forEach { it.identificationId = identificationWithDocument.identification.id }

        return identificationDao.insert(identificationWithDocument.identification)
                .andThen(documentDao.insert(identificationWithDocument.documents))
    }

}