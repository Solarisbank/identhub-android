package de.solarisbank.identhub.data.contract

import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.data.entity.IdentificationWithDocument
import io.reactivex.Completable
import io.reactivex.Single

interface ContractSignLocalDataSource {
    fun deleteAll(): Completable
    fun getAllDocuments(): Single<List<Document>>
    fun getIdentification(): Single<Identification>
    fun insert(identificationWithDocument: IdentificationWithDocument): Completable
}