package de.solarisbank.identhub.feature.util

import de.solarisbank.identhub.domain.data.dto.IbanVerificationModel
import de.solarisbank.identhub.domain.data.dto.ProcessingVerificationDto
import de.solarisbank.identhub.verfication.bank.SealedVerificationState
import de.solarisbank.identhub.verfication.bank.VerificationState
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationFragment
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.domain.model.IdentificationUiModel


fun IbanVerificationModel.toVerificationState(): VerificationState {
    return when (this) {
        is IbanVerificationModel.IbanVerificationSuccessful ->
            SealedVerificationState.IbanVerificationSuccessful(this.bankIdentificationUrl, this.nextStep)
        is IbanVerificationModel.InvalidBankIdError ->
            SealedVerificationState.InvalidBankIdError(this.nextStep, this.retryAllowed)
        is IbanVerificationModel.AlreadyIdentifiedSuccessfullyError ->
            SealedVerificationState.AlreadyIdentifiedSuccessfullyError()
        is IbanVerificationModel.GenericError -> SealedVerificationState.GenericError()
        is IbanVerificationModel.ExceedMaximumAttemptsError -> SealedVerificationState.ExceedMaximumAttemptsError()
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
