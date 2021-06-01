package de.solarisbank.sdk.core.presentation

data class IdentificationUiModel(
        val id: String,
        val status: String?,
        val nextStep: String?,
        val method: String? = null
)