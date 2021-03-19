package de.solarisbank.identhub.data.verification.bank

import de.solarisbank.identhub.data.entity.IdentificationWithDocument
import io.reactivex.Completable

interface VerificationBankLocalDataSource {
    fun insert(identificationWithDocument: IdentificationWithDocument): Completable
}