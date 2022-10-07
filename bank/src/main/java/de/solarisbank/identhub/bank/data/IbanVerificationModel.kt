package de.solarisbank.identhub.bank.data

sealed class IbanVerificationModel {
    class IbanVerificationSuccessful(val bankIdentificationUrl: String?, val nextStep: String?) : IbanVerificationModel()
    class InvalidBankIdError(val nextStep: String, val retryAllowed: Boolean) : IbanVerificationModel()
    object AlreadyIdentifiedSuccessfullyError : IbanVerificationModel()
    object ExceedMaximumAttemptsError : IbanVerificationModel()
    object GenericError : IbanVerificationModel()
}