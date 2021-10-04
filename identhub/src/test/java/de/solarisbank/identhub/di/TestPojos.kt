package de.solarisbank.identhub.di

import de.solarisbank.sdk.data.dto.InitializationDto
import de.solarisbank.sdk.data.dto.PartnerSettingsDto

val basicInitializationDto = InitializationDto(
    "bank/iban",
"fourthline/simplified",
5, "SolarisBankTest",
    PartnerSettingsDto(true)
)