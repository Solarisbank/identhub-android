package de.solarisbank.sdk.fourthline.domain

import de.solarisbank.sdk.fourthline.data.dto.LocationDto
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument.NATIONAL_ID_CARD
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument.valueOf
import de.solarisbank.sdk.fourthline.domain.dto.PersonDataStateDto

fun PersonDataDto.appliedDocuments(): List<AppliedDocument>? {
    return this.supportedDocuments
            ?.filter { !(it?.issuingCountries?.isEmpty() ?: true) }
            ?.mapNotNull { it?.type?.appliedDocument() }
            ?.filter { it.isSupported }
            ?.toList()
}

private fun String.appliedDocument(): AppliedDocument {
    return try {
        valueOf(replace(" ", "_").toUpperCase())
    } catch (e: IllegalArgumentException) {
        NATIONAL_ID_CARD
    }
}

fun LocationDto.toPersonDataStateDto(): PersonDataStateDto {
    return when (this) {
        is LocationDto.NETWORK_NOT_ENABLED_ERROR -> PersonDataStateDto.LOCATION_CLIENT_NOT_ENABLED_ERROR
        is LocationDto.SUCCESS -> PersonDataStateDto.LOCATION_SUCCESS(this.location)
        is LocationDto.LOCATION_CLIEN_NOT_ENABLED_ERROR -> PersonDataStateDto.LOCATION_CLIENT_NOT_ENABLED_ERROR
        is LocationDto.LOCATION_FETCHING_ERROR -> PersonDataStateDto.LOCATION_FETCHING_ERROR
    }
}