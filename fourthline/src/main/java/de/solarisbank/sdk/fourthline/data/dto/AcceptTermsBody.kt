package de.solarisbank.sdk.fourthline.data.dto

import com.squareup.moshi.Json

data class AcceptTermsBody(
    @Json(name = "document_id")
    val documentId: String
)
