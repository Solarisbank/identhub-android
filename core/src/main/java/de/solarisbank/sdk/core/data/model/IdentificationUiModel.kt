package de.solarisbank.sdk.core.data.model

data class IdentificationUiModel(
        val id: String,
        val status: String?,
        val failureReason: String?,
        val nextStep: String?,
        val method: String? = null
)