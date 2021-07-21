package de.solarisbank.sdk.core.data.utils

import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.sdk.core.data.model.IdentificationUiModel

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