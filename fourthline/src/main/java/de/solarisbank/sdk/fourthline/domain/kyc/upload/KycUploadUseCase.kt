package de.solarisbank.sdk.fourthline.domain.kyc.upload

import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.entity.Status
import de.solarisbank.identhub.data.network.transformResult
import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.identhub.domain.session.NextStepSelector
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.Type
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRepository
import de.solarisbank.sdk.fourthline.domain.dto.KycUploadStatusDto
import io.reactivex.Single
import timber.log.Timber
import java.io.File

class KycUploadUseCase  (
    private val kycUploadRepository: KycUploadRepository,
    private val sessionUrlRepositoryRepository: SessionUrlRepository,
    private val identificationPollingStatusUseCase: IdentificationPollingStatusUseCase,
    override val identityInitializationRepository: IdentityInitializationRepository
) : NextStepSelector {

//    fun setBaseUrl(baseUrl: String) {
//        sessionUrlRepositoryRepository.save(baseUrl)
//    }

    fun uploadKyc(file: File): Single<KycUploadStatusDto> {
        return kycUploadRepository.uploadKyc(file)
            .doOnSuccess { kycUploadResponseDto ->
                Timber.d("uploadKyc(), upload successful, kycUploadResponseDto : $kycUploadResponseDto")
            }.doOnError {
                Timber.e(it,"uploadKyc(), upload failed")
            }
            .flatMap { pollKycProcessingResult() }
    }

    private fun pollKycProcessingResult(): Single<KycUploadStatusDto> {
        return identificationPollingStatusUseCase.pollIdentificationStatus()
            .map { NavigationalResult(it) }
            .transformResult()
            .map { result ->
                if (result.succeeded && result.data != null) {
                    Timber.d("pollKycProcessingResult(), 1 : $result ")
                    val data = result.data!!
                    val nextStep = selectNextStep(data.nextStep, data.fallbackStep)
                    if (data.status == Status.SUCCESSFUL.label) {
                        Timber.d("pollKycProcessingResult(), 2")
                        return@map KycUploadStatusDto.FinishIdentSuccess(data.id)
                    } else if (data.status == Status.AUTHORIZATION_REQUIRED.label && nextStep != null) {
                        Timber.d("pollKycProcessingResult(), 3")
                        return@map KycUploadStatusDto.ToNextStepSuccess(nextStep)
                    } else {
                        Timber.d("pollKycProcessingResult(), 4")
                        val providerStatusCode = data.providerStatusCode?.toIntOrNull()

                        if (providerStatusCode != null) {
                            Timber.d("pollKycProcessingResult(), 5")
                            if (providerStatusCode >= 4000) {
                                Timber.d("pollKycProcessingResult(), 6")
                                return@map KycUploadStatusDto.ProviderErrorFraud
                            } else {
                                Timber.d("pollKycProcessingResult(), 7")
                                return@map KycUploadStatusDto.ProviderErrorNotFraud
                            }
                        } else {
                            Timber.d("pollKycProcessingResult(), 8")
                            return@map KycUploadStatusDto.GenericError
                        }
                    }
                } else if (result is Result.Error) {
                    Timber.d("pollKycProcessingResult(), 9 : ${result.type} ")
                    when (result.type) {
                        is Type.PreconditionFailed -> {
                            Timber.d("pollKycProcessingResult(), 10")
                            return@map KycUploadStatusDto.PreconditionsFailedError
                        }
                        else -> {
                            //todo could be spread
                            Timber.d("pollKycProcessingResult(), 11")
                            return@map KycUploadStatusDto.GenericError
                        }
                    }
                } else {
                    Timber.w("pollKycProcessingResult(), 12")
                    return@map KycUploadStatusDto.GenericError
                }
            }
    }

}