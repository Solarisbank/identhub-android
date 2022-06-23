package de.solarisbank.sdk.logger.domain.model

import com.squareup.moshi.Json


data class LogContent(
    @Json(name = "content")
    val content: List<LogJson>
)