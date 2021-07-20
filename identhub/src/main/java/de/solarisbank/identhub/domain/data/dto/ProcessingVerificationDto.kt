package de.solarisbank.identhub.domain.data.dto

sealed class ProcessingVerificationDto {
    class VerificationSuccessful(val id: String): ProcessingVerificationDto()
    class PaymentInitAuthPersonError(val nextStep: String) : ProcessingVerificationDto() //8. payment init auth person
    class PaymentInitFailed(val nextStep: String) : ProcessingVerificationDto() // 9
    class PaymentInitExpired(val nextStep: String) : ProcessingVerificationDto() // 10
    object GenericError: ProcessingVerificationDto()
}