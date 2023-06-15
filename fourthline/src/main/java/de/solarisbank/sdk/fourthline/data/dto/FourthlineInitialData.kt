package de.solarisbank.sdk.fourthline.data.dto

import de.solarisbank.sdk.data.dto.IdentificationDto

data class FourthlineInitialData(
    val identification: IdentificationDto,
    val terms: TermsAndConditions?,
    val ip: IpDto
)
