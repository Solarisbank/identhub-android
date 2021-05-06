package de.solarisbank.sdk.fourthline.data.kyc.upload

import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.sdk.fourthline.data.dto.KycUploadResponseDto
import de.solarisbank.sdk.fourthline.data.getPartFile
import io.reactivex.Single
import java.io.File


class KycUploadRetrofitDataSource(private val kycUploadApi: KycUploadApi) {

    fun uploadKYC(identificationDto: IdentificationDto, file: File): Single<KycUploadResponseDto> {
        return kycUploadApi.uploadKYC(url+identificationDto.id+urlEnd, file.getPartFile())
    }

    companion object {
        private const val url = "/fourthline_identification/"
        private const val urlEnd = "/data"
    }
}