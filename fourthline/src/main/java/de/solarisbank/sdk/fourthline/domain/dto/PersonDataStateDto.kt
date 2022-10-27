package de.solarisbank.sdk.fourthline.domain.dto

import de.solarisbank.sdk.fourthline.data.dto.Location
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument

sealed class PersonDataStateDto {
    object UPLOADING : PersonDataStateDto()
    class SUCCEEDED(val docs: List<AppliedDocument>) : PersonDataStateDto()
    object EMPTY_DOCS_LIST_ERROR: PersonDataStateDto()
    data class LOCATION_SUCCESS(val location: Location): PersonDataStateDto()
    object NETWORK_NOT_ENABLED_ERROR: PersonDataStateDto()
    object LOCATION_CLIENT_NOT_ENABLED_ERROR: PersonDataStateDto()
    object LOCATION_FETCHING_ERROR: PersonDataStateDto()
    object GENERIC_ERROR : PersonDataStateDto()

    object RETRY_LOCATION_FETCHING : PersonDataStateDto()
}