package de.solarisbank.identhub.bank.data

sealed class ProcessingVerificationDto {
    class VerificationSuccessful(val id: String, val nextStep: String): ProcessingVerificationDto()
    class PaymentInitAuthPersonError(val nextStep: String) : ProcessingVerificationDto() //8. payment init auth person
    class PaymentInitFailed(val nextStep: String) : ProcessingVerificationDto() // 9
    class PaymentInitExpired(val nextStep: String) : ProcessingVerificationDto() // 10
    object GenericError: ProcessingVerificationDto()
}