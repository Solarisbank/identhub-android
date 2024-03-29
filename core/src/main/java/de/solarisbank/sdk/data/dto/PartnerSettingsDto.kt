package de.solarisbank.sdk.data.dto

import com.squareup.moshi.Json

data class PartnerSettingsDto(
    @Json(name = "default_to_fallback_step") var defaultToFallbackStep: Boolean?
)
