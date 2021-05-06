package de.solarisbank.sdk.fourthline.domain.kyc.upload

import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.sdk.fourthline.data.dto.KycUploadResponseDto
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRepository
import io.reactivex.Single
import java.io.File

class KycUploadUseCase(
        private val kycUploadRepository: KycUploadRepository,
        private val sessionUrlRepositoryRepository: SessionUrlRepository
        ) {

    fun setBaseUrl(baseUrl: String) {
        sessionUrlRepositoryRepository.save(baseUrl)
    }

    fun uploadKyc(file: File): Single<KycUploadResponseDto> {
        return kycUploadRepository.uploadKyc(file)
    }

    fun pollIdentification(): Single<IdentificationDto> {
        return kycUploadRepository.pollIdentificationStatus()
    }

}