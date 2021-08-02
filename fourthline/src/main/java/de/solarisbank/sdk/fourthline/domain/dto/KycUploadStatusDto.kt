package de.solarisbank.sdk.fourthline.domain.dto

sealed class KycUploadStatusDto {
    data class ToNextStepSuccess(val nextStep: String): KycUploadStatusDto()
    data class FinishIdentSuccess(val id: String): KycUploadStatusDto()
    object ProviderErrorNotFraud: KycUploadStatusDto()
    object ProviderErrorFraud: KycUploadStatusDto()
    object PreconditionsFailedError: KycUploadStatusDto()
    object GenericError: KycUploadStatusDto()
}