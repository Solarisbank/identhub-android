package de.solarisbank.sdk.logger.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


data class LogJson(
    @Json(name = "detail")
    val detail: String? = null,
    @Json(name = "level")
    val type: String? = null,
    @Json(name = "category")
    val category: String? = null,
    @Json(name = "timestamp")
    val timeStamp: String = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss.SSSSSS",
        Locale.getDefault()
    ).format(Date()).toString()
)