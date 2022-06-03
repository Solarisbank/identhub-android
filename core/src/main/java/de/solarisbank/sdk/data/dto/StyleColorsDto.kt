package de.solarisbank.sdk.data.dto

import java.io.Serializable

data class StyleColorsDto(
    val primary: String?,
    val primaryDark: String?,
    val secondary: String?,
    val secondaryDark: String?
): Serializable