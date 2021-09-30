package de.solarisbank.sdk.fourthline.data.kyc.upload

import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.fourthline.data.dto.KycUploadResponseDto
import io.reactivex.Single
import timber.log.Timber
import java.io.File

class KycUploadRepository(
    private val identificationLocalDataSource: IdentificationLocalDataSource,
    private val kycUploadRetrofitDataSource: KycUploadRetrofitDataSource
        ) {

    fun uploadKyc(file: File): Single<KycUploadResponseDto> {
        Timber.d("identificationRoomDataSource.getIdentification() : ${identificationLocalDataSource.getIdentificationDto()}")
        return identificationLocalDataSource.getIdentificationDto()
                .flatMap {
                    identification ->
                    identificationLocalDataSource.saveIdentification(identification.apply { status = Status.UPLOAD.label })
                            .andThen(kycUploadRetrofitDataSource.uploadKYC(identification.id, file))
                            .doOnSuccess{ identificationLocalDataSource.saveIdentification(identification.apply { status = Status.PENDING.label }) }
                }
    }

}