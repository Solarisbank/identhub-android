package de.solarisbank.sdk.fourthline.data.location

import de.solarisbank.sdk.fourthline.data.dto.LocationDto
import io.reactivex.Single

interface LocationRepository {
    fun getLocation(): Single<LocationDto>
}