package de.solarisbank.sdk.data.datasource

import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Completable
import io.reactivex.Single

interface IdentificationLocalDataSource {

    suspend fun getIdentification(): IdentificationDto?
    suspend fun storeIdentification(identification: IdentificationDto)

    fun obtainIdentificationDto(): Single<IdentificationDto>

    fun saveIdentification(identificationDto: IdentificationDto): Completable

    fun deleteIdentification(): Completable
}