package de.solarisbank.sdk.fourthline.domain

import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.dto.LocationResult
import de.solarisbank.sdk.fourthline.data.dto.AppliedDocument
import de.solarisbank.sdk.fourthline.data.dto.AppliedDocument.NATIONAL_ID_CARD
import de.solarisbank.sdk.fourthline.data.dto.AppliedDocument.valueOf
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
        valueOf(replace(" ", "_").uppercase())
    } catch (e: IllegalArgumentException) {
        NATIONAL_ID_CARD
    }
}

fun LocationResult.toPersonDataStateDto(): PersonDataStateDto {
    return when (this) {
        is LocationResult.NetworkNotEnabledError -> PersonDataStateDto.LOCATION_CLIENT_NOT_ENABLED_ERROR
        is LocationResult.Success -> PersonDataStateDto.LOCATION_SUCCESS(location)
        is LocationResult.LocationClientNotEnabledError -> PersonDataStateDto.LOCATION_CLIENT_NOT_ENABLED_ERROR
        is LocationResult.LocationFetchingError -> PersonDataStateDto.LOCATION_FETCHING_ERROR
    }
}