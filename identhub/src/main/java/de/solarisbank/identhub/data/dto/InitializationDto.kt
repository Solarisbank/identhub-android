package de.solarisbank.identhub.data.dto

import com.squareup.moshi.Json

data class InitializationDto(
        @Json(name = "first_step") var firstStep: String
)
