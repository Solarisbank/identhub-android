package de.solarisbank.sdk.fourthline.data.kyc.upload

import de.solarisbank.sdk.fourthline.data.dto.KycUploadResponseDto
import de.solarisbank.sdk.fourthline.data.getPartFile
import io.reactivex.Single
import java.io.File
import java.net.URI

interface KycUploadDataSource {
    fun uploadKYC(identificationId: String, fileUri: URI): Single<KycUploadResponseDto>
}

class KycUploadRetrofitDataSource(private val kycUploadApi: KycUploadApi): KycUploadDataSource {

    override fun uploadKYC(identificationId: String, fileUri: URI): Single<KycUploadResponseDto> {
        return kycUploadApi.uploadKYC(url+identificationId+urlEnd, File(fileUri).getPartFile())
    }

    companion object {
        private const val url = "/fourthline_identification/"
        private const val urlEnd = "/data"
    }
}