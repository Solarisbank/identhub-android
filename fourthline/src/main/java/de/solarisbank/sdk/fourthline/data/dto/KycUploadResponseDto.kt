package de.solarisbank.sdk.fourthline.data.dto

import com.squareup.moshi.Json
import java.util.*

data class KycUploadResponseDto(
        var id: String,
        var name: String?,
        @Json(name = "content_type")var contentType: String?,
        @Json(name = "document_type")var documentType: String?,
        var size: Int?,
        @Json(name = "customer_accessible") var customerAccessible: Boolean? = null,
        @Json(name = "created_at") var createdAt: Date? = null
)
