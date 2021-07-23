package de.solarisbank.sdk.fourthline.domain.location

import android.location.Location
import de.solarisbank.sdk.fourthline.data.location.LocationRepository
import io.reactivex.Single

class LocationUseCase(private val locationRepository: LocationRepository) {

    fun getLocation(): Single<Location> {
        return locationRepository.getLocation()
    }

}