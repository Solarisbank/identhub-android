package de.solarisbank.identhub.data.dto

import com.squareup.moshi.Json
import java.util.*

data class IdentificationDto(
        var id: String,
        var reference: String? = null,
        var url: String?,
        var status: String,
        @Json(name = "failure_reason") var failureReason: String? = null,
        var method: String? = null,
        @Json(name = "next_step") var nextStep: String? = null,
        @Json(name = "completed_at") var completedAt: Date? = null,
        @Json(name = "proof_of_address_type") var proofOfAddressType: String? = null,
        @Json(name = "proof_of_address_issued_at") var proofOfAddressIssuedAt: Date? = null,
        @Json(name = "terms_and_conditions_signed_at") var termsAndConditionsSignedAt: Date? = null,
        @Json(name = "authorization_expires_at") var authorizationExpiresAt: Date? = null,
        @Json(name = "confirmation_expires_at") var confirmationExpiresAt: Date? = null,
        @Json(name = "estimated_waiting_time") var estimatedWaitingTime: String? = null,
        val documents: List<DocumentDto>?) {
}