package de.solarisbank.sdk.fourthline.data.location

import de.solarisbank.sdk.fourthline.data.dto.LocationDto
import io.reactivex.Single

class LocationRepositoryImpl(private val locationDataSource: LocationDataSource) : LocationRepository{

    override fun getLocation(): Single<LocationDto> {
        return locationDataSource.getLocation()
    }

}