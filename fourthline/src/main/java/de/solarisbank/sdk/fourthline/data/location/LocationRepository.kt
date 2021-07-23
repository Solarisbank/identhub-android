package de.solarisbank.sdk.fourthline.data.location

import android.location.Location
import io.reactivex.Single

interface LocationRepository {
    fun getLocation(): Single<Location>
}