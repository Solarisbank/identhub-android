package de.solarisbank.identhub.domain.data.dto

import com.squareup.moshi.Json

data class InitializationDto(
        @Json(name = "first_step") var firstStep: String,
        @Json(name = "fallback_step") var fallbackStep: String?,
        @Json(name = "allowed_retries") var allowedRetries: Int
)
