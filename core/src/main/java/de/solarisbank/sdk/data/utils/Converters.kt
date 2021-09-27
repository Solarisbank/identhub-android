package de.solarisbank.sdk.data.utils

import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.Identification
import de.solarisbank.sdk.domain.model.IdentificationUiModel

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

fun IdentificationDto.toIdentification(): Identification {
    return Identification(
            id = this.id,
            status = this.status,
            nextStep = this.nextStep,
            fallbackStep = this.fallbackStep,
            url = this.url ?: "", //todo make it nullable in db
            method = this.method)
}
