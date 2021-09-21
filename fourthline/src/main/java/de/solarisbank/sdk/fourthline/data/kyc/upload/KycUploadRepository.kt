package de.solarisbank.sdk.fourthline.data.kyc.upload

import de.solarisbank.identhub.session.data.identification.IdentificationRoomDataSource
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.fourthline.data.dto.KycUploadResponseDto
import io.reactivex.Single
import timber.log.Timber
import java.io.File

class KycUploadRepository(
        private val identificationRoomDataSource: IdentificationRoomDataSource,
        private val kycUploadRetrofitDataSource: KycUploadRetrofitDataSource
        ) {

    fun uploadKyc(file: File): Single<KycUploadResponseDto> {
        Timber.d("identificationRoomDataSource.getIdentification() : ${identificationRoomDataSource.getIdentification()}")
        return identificationRoomDataSource.getIdentification()
                .flatMap {
                    identification ->
                    identificationRoomDataSource.insert(identification.apply { status = Status.UPLOAD.label })
                            .andThen(kycUploadRetrofitDataSource.uploadKYC(identification.id, file))
                            .doOnSuccess{ identificationRoomDataSource.insert(identification.apply { status = Status.PENDING.label }) }
                }
    }

}