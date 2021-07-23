package de.solarisbank.sdk.fourthline.data.location

import android.content.Context
import de.solarisbank.sdk.core.di.internal.Factory

class LocationDataSourceFactory private constructor(
        private val applicationContext: Context
) : Factory<LocationDataSource> {

    override fun get(): LocationDataSource {
        return LocationDataSourceImpl(applicationContext)
    }

    companion object {
        fun create(applicationContext: Context): LocationDataSourceFactory {
            return LocationDataSourceFactory(applicationContext)
        }
    }
}