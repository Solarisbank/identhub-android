package de.solarisbank.sdk.fourthline.data.location

import de.solarisbank.sdk.fourthline.data.dto.LocationResult
import io.reactivex.Single

interface LocationDataSource {
    fun getLocation(): Single<LocationResult>
}