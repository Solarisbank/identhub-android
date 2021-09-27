package de.solarisbank.sdk.data.repository

import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRoomDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.MobileNumberDto
import de.solarisbank.sdk.data.entity.Identification
import io.reactivex.Completable
import io.reactivex.Single

class IdentificationRepository(
    private val identificationRoomDataSource: IdentificationRoomDataSource,
    private val identificationRetrofitDataSource: IdentificationRetrofitDataSource,
    private val mobileNumberDataSource: MobileNumberDataSource
        ) {

        fun getStoredIdentification(): Single<Identification> {
                return identificationRoomDataSource.getIdentification()
        }

        fun getRemoteIdentificationDto(identificatioId: String): Single<IdentificationDto> {
                return identificationRetrofitDataSource.getIdentification(identificatioId)
        }

        fun insertIdentification(identification: Identification): Completable {
                return identificationRoomDataSource.insert(identification)
        }

        fun getMobileNumber(): Single<MobileNumberDto> {
                return mobileNumberDataSource.getMobileNumber()
        }


}