package de.solarisbank.sdk.data.dto

import com.squareup.moshi.Json
import java.io.Serializable

data class InitializationInfoDto(
        @Json(name = "terms_and_conditions_pre_accepted") val termsAccepted: Boolean,
        @Json(name = "verified_mobile_number") val phoneVerified: Boolean,
        @Json(name = "style") val style: StyleDto?,
        @Json(name = "sdk_logging") val sdkLogging: Boolean?,
        @Json(name = "secondary_document_required") val secondaryDocScanRequired: Boolean?,
        @Json(name = "orca_enabled") val orcaEnabled: Boolean?
)
