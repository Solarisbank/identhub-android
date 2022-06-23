package de.solarisbank.sdk.logger.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


data class LogJson(
    @Json(name = "detail")
    val detail: String? = null,
    @Json(name = "type")
    val type: String? = null,
    @Json(name = "category")
    val category: String? = null
)