package de.solarisbank.identhub.data.verification.bank

import de.solarisbank.sdk.data.entity.Identification
import de.solarisbank.sdk.data.entity.IdentificationWithDocument
import io.reactivex.Completable
import io.reactivex.Single

interface VerificationBankLocalDataSource {
    fun getIdentificationDto(): Single<Identification>
    fun insert(identificationWithDocument: IdentificationWithDocument): Completable
    fun deleteALl(): Completable
}