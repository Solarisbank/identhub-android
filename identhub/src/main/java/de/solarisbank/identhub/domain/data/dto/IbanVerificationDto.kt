package de.solarisbank.identhub.domain.data.dto

sealed class IbanVerificationDto {
    class IbanVerificationSuccessful(val bankIdentificationUrl: String?, val nextStep: String?) : IbanVerificationDto()
    class InvalidBankIdError(val nextStep: String, val retryAllowed: Boolean) : IbanVerificationDto()
    object AlreadyIdentifiedSuccessfullyError : IbanVerificationDto()
    object ExceedMaximumAttemptsError : IbanVerificationDto()
    object GenericError : IbanVerificationDto()
}