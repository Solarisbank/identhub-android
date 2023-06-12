package de.solarisbank.sdk.data.datasource

import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single

interface IdentificationRemoteDataSource {
    fun getIdentification(identificationId: String): Single<IdentificationDto>
    suspend fun fetchIdentification(identificationId: String): IdentificationDto

}