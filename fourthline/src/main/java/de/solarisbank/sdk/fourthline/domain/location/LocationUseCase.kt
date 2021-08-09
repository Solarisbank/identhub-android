package de.solarisbank.sdk.fourthline.domain.location

import de.solarisbank.sdk.fourthline.data.dto.LocationDto
import de.solarisbank.sdk.fourthline.data.location.LocationRepository
import io.reactivex.Single

class LocationUseCase(private val locationRepository: LocationRepository) {

    fun getLocation(): Single<LocationDto> {
        return locationRepository.getLocation()
    }

}