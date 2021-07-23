package de.solarisbank.sdk.fourthline.domain.location

import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.fourthline.data.location.LocationRepository

class LocationUseCaseFactory private constructor(
        private val locationRepository: LocationRepository
) : Factory<LocationUseCase> {

    override fun get(): LocationUseCase {
        return LocationUseCase(locationRepository)
    }

    companion object {
        @JvmStatic
        fun create(locationRepository: LocationRepository): LocationUseCaseFactory {
            return LocationUseCaseFactory(locationRepository)
        }
    }

}