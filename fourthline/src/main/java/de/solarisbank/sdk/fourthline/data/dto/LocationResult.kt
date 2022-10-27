package de.solarisbank.sdk.fourthline.data.dto

data class Location(val latitude: Double, val longitude: Double) {
    constructor(location: android.location.Location) : this(location.latitude, location.longitude)
}

sealed class LocationResult {
    data class Success(val location: Location): LocationResult()
    object NetworkNotEnabledError: LocationResult()
    object LocationClientNotEnabledError: LocationResult()
    object LocationFetchingError: LocationResult()
}