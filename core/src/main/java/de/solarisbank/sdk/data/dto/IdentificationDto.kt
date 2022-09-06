package de.solarisbank.sdk.data.dto

import com.squareup.moshi.Json
import java.util.*

data class IdentificationDto(
        var id: String,
        var reference: String? = null,
        var url: String?,
        var status: String,
        @Json(name = "failure_reason")
        var failureReason: String? = null,
        var method: String? = null,
        @Json(name = "next_step") var nextStep: String? = null,
        @Json(name = "fallback_step") var fallbackStep: String? = null,
        @Json(name = "provider_status_code") var providerStatusCode: String? = null,
        val documents: List<DocumentDto>?)