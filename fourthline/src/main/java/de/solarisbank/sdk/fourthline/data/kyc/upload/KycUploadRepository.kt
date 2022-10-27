package de.solarisbank.sdk.fourthline.data.kyc.upload

import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.fourthline.data.dto.KycUploadResponseDto
import io.reactivex.Single
import timber.log.Timber
import java.net.URI

class KycUploadRepository(
    private val identificationLocalDataSource: IdentificationLocalDataSource,
    private val kycUploadDataSource: KycUploadDataSource
        ) {

    fun uploadKyc(fileUri: URI): Single<KycUploadResponseDto> {
        Timber.d("identificationRoomDataSource.getIdentification() : ${identificationLocalDataSource.obtainIdentificationDto()}")
        return identificationLocalDataSource.obtainIdentificationDto()
                .flatMap {
                    identification ->
                    identificationLocalDataSource.saveIdentification(identification.apply { status = Status.UPLOAD.label })
                            .andThen(kycUploadDataSource.uploadKYC(identification.id, fileUri))
                            .doOnSuccess{ identificationLocalDataSource.saveIdentification(identification.apply { status = Status.PENDING.label }) }
                }
    }

}