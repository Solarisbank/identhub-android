package de.solarisbank.sdk.domain.model
//todo remove
data class IdentificationUiModel(
        val id: String,
        val status: String?,
        val failureReason: String?,
        val nextStep: String?,
        val fallbackStep: String?,
        val method: String? = null
)