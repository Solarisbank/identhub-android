package de.solarisbank.sdk.logger.domain.model

import com.squareup.moshi.Json

data class LogJson(
    @Json(name = "detail")
    val detail: String? = null,
    @Json(name = "level")
    val type: String? = null,
    @Json(name = "category")
    val category: String? = null,
    @Json(name = "timestamp")
    val timestamp: String
)