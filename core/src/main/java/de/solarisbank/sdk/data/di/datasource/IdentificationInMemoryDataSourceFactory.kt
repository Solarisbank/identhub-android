package de.solarisbank.sdk.data.di.datasource

import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.sdk.feature.di.internal.Factory

class IdentificationInMemoryDataSourceFactory private constructor()
    : Factory<IdentificationInMemoryDataSource> {

    override fun get(): IdentificationInMemoryDataSource {
        return IdentificationInMemoryDataSource()
    }

    companion object {
        @JvmStatic
        fun create(): IdentificationInMemoryDataSourceFactory {
            return IdentificationInMemoryDataSourceFactory()
        }
    }

}