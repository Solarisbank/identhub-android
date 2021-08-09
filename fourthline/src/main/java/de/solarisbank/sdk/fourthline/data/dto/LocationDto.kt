package de.solarisbank.sdk.fourthline.data.dto

import android.location.Location

sealed class LocationDto {
    class SUCCESS(val location: Location): LocationDto()
    object NETWORK_NOT_ENABLED_ERROR: LocationDto()
    object LOCATION_CLIEN_NOT_ENABLED_ERROR: LocationDto()
    object LOCATION_FETCHING_ERROR: LocationDto()
}