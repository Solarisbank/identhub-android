package de.solarisbank.sdk.fourthline.data.location

import de.solarisbank.sdk.fourthline.data.dto.LocationResult
import io.reactivex.Single

class LocationRepositoryImpl(private val locationDataSource: LocationDataSource) : LocationRepository{

    override fun getLocation(): Single<LocationResult> {
        return locationDataSource.getLocation()
    }

}