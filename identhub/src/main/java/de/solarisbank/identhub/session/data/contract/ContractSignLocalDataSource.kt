package de.solarisbank.identhub.session.data.contract

import de.solarisbank.sdk.data.entity.Document
import de.solarisbank.sdk.data.entity.Identification
import de.solarisbank.sdk.data.entity.IdentificationWithDocument
import io.reactivex.Completable
import io.reactivex.Single

interface ContractSignLocalDataSource {
    fun deleteAll(): Completable
    fun getAllDocuments(): Single<List<Document>>
    fun getIdentification(): Single<Identification>
    fun insert(identificationWithDocument: IdentificationWithDocument): Completable
}