package de.solarisbank.identhub.startup.model

import com.squareup.moshi.Json

data class VerificationPhoneResponse(
    val id: String,
    val number: String,
    @Json(name = "verified") val isVerified: Boolean,
)