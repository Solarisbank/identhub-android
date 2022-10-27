package de.solarisbank.sdk.fourthline.domain.location

import de.solarisbank.sdk.fourthline.data.dto.LocationResult
import de.solarisbank.sdk.fourthline.data.location.LocationRepository
import io.reactivex.Single

class LocationUseCase(private val locationRepository: LocationRepository) {

    fun getLocation(): Single<LocationResult> {
        return locationRepository.getLocation()
    }

}