package de.solarisbank.sdk.fourthline.data.location

import de.solarisbank.sdk.core.di.internal.Factory

class LocationRepositoryFactory private constructor(
        private val locationDataSource: LocationDataSource
) : Factory<LocationRepository>{

    override fun get(): LocationRepository {
        return LocationRepositoryImpl(locationDataSource)
    }

    companion object {
        fun create(locationDataSource: LocationDataSource): LocationRepositoryFactory {
            return LocationRepositoryFactory(locationDataSource)
        }
    }
}