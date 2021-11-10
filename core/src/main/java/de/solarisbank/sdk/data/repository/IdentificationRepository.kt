package de.solarisbank.sdk.data.repository

import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.MobileNumberDto
import io.reactivex.Completable
import io.reactivex.Single

class IdentificationRepository(
    private val identificationRoomDataSource: IdentificationLocalDataSource,
    private val identificationRetrofitDataSource: IdentificationRetrofitDataSource,
    private val mobileNumberDataSource: MobileNumberDataSource
        ) {

        fun getStoredIdentification(): Single<IdentificationDto> {
                return identificationRoomDataSource.obtainIdentificationDto()
        }

        fun getRemoteIdentificationDto(identificatioId: String): Single<IdentificationDto> {
                return identificationRetrofitDataSource.getIdentification(identificatioId)
        }

        fun insertIdentification(identification: IdentificationDto): Completable {
                return identificationRoomDataSource.saveIdentification(identification)
        }

        fun getMobileNumber(): Single<MobileNumberDto> {
                return mobileNumberDataSource.getMobileNumber()
        }


}