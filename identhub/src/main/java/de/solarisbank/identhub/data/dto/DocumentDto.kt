package de.solarisbank.identhub.data.dto

import com.squareup.moshi.Json
import java.util.*

data class DocumentDto(
        val id: String,
        val name: String,
        @Json(name = "content_type")
        val contentType: String,
        @Json(name = "document_type")
        val documentType: String,
        val size: Long,
        @Json(name = "customer_accessible")
        val isCustomerAccessible: Boolean,
        @Json(name = "created_at")
        val createdAt: Date? = null,
        @Json(name = "deleted_at")
        val deletedAt: Date? = null) {
}