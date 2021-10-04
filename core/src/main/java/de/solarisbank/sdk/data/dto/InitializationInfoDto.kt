package de.solarisbank.sdk.data.dto

import com.squareup.moshi.Json

data class InitializationInfoDto(
        @Json(name = "terms_and_conditions_pre_accepted") val termsAccepted: Boolean,
        @Json(name = "verified_mobile_number") val phoneVerified: Boolean,
        @Json(name = "style") val style: StyleDto?
)
