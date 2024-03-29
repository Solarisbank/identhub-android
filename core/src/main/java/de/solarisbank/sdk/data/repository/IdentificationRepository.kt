package de.solarisbank.sdk.data.repository

import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRemoteDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Completable
import io.reactivex.Single

class IdentificationRepository(
    private val identificationRoomDataSource: IdentificationLocalDataSource,
    private val identificationRetrofitDataSource: IdentificationRemoteDataSource) {

        fun getStoredIdentification(): Single<IdentificationDto> {
                return identificationRoomDataSource.obtainIdentificationDto()
        }

        suspend fun getRemoteIdentification(identificationId: String): IdentificationDto {
            return identificationRetrofitDataSource.fetchIdentification(identificationId)
        }

        fun getRemoteIdentificationDto(identificatioId: String): Single<IdentificationDto> {
                return identificationRetrofitDataSource.getIdentification(identificatioId)
        }

        fun insertIdentification(identification: IdentificationDto): Completable {
                return identificationRoomDataSource.saveIdentification(identification)
        }
}