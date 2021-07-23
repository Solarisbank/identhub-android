package de.solarisbank.sdk.fourthline.data.location

import android.location.Location
import io.reactivex.Single

interface LocationDataSource {
    fun getLocation(): Single<Location>
}