package de.solarisbank.sdk.fourthline.data.location

import android.location.Location
import io.reactivex.Single

class LocationRepositoryImpl(private val locationDataSource: LocationDataSource) : LocationRepository{

    override fun getLocation(): Single<Location> {
        return locationDataSource.getLocation()
    }

}