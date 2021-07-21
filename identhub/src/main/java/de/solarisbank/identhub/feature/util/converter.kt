package de.solarisbank.identhub.feature.util

import de.solarisbank.identhub.domain.data.dto.IbanVerificationDto
import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.identhub.domain.data.dto.ProcessingVerificationDto
import de.solarisbank.identhub.verfication.bank.SealedVerificationState
import de.solarisbank.identhub.verfication.bank.VerificationState
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationFragment
import de.solarisbank.sdk.core.data.model.IdentificationUiModel


fun IbanVerificationDto.toVerificationState(): VerificationState {
    return when (this) {
        is IbanVerificationDto.IbanVerificationSuccessful ->
            SealedVerificationState.IbanVerificationSuccessful(this.bankIdentificationUrl, this.nextStep)
        is IbanVerificationDto.InvalidBankIdError ->
            SealedVerificationState.InvalidBankIdError(this.nextStep, this.retryAllowed)
        is IbanVerificationDto.AlreadyIdentifiedSuccessfullyError ->
            SealedVerificationState.AlreadyIdentifiedSuccessfullyError()
        is IbanVerificationDto.GenericError -> SealedVerificationState.GenericError()
        is IbanVerificationDto.ExceedMaximumAttemptsError -> SealedVerificationState.ExceedMaximumAttemptsError()
    }
}

fun ProcessingVerificationDto.toProcessingVerificationState(): ProcessingVerificationFragment.ProcessingVerificationResult {
    return when (this) {
        is ProcessingVerificationDto.VerificationSuccessful ->
            ProcessingVerificationFragment.ProcessingVerificationResult.VerificationSuccessful(this.id)
        is ProcessingVerificationDto.PaymentInitAuthPersonError ->
            ProcessingVerificationFragment.ProcessingVerificationResult.PaymentInitExpiredError(this.nextStep)
        is ProcessingVerificationDto.PaymentInitFailed ->
            ProcessingVerificationFragment.ProcessingVerificationResult.PaymentInitFailedError(this.nextStep)
        is ProcessingVerificationDto.PaymentInitExpired ->
            ProcessingVerificationFragment.ProcessingVerificationResult.PaymentInitExpiredError(this.nextStep)
        is ProcessingVerificationDto.GenericError ->
            ProcessingVerificationFragment.ProcessingVerificationResult.GenericError
    }
}

fun IdentificationDto.toIdentificationUiModel(): IdentificationUiModel {
    return IdentificationUiModel(
            id = this.id,
            status = this.status,
            failureReason = this.failureReason,
            nextStep = this.nextStep,
            fallbackStep = this.fallbackStep,
            method = this.method
    )
}
