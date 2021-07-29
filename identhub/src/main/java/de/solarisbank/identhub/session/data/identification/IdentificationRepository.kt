package de.solarisbank.identhub.session.data.identification

import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.identhub.domain.data.dto.MobileNumberDto
import de.solarisbank.identhub.session.data.mobile.number.MobileNumberDataSource
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