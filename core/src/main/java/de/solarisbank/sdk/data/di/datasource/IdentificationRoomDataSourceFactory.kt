package de.solarisbank.sdk.data.di.datasource

import de.solarisbank.sdk.data.dao.IdentificationDao
import de.solarisbank.sdk.data.datasource.IdentificationRoomDataSource
import de.solarisbank.sdk.data.di.IdentificationModule
import de.solarisbank.sdk.feature.di.internal.Factory

class IdentificationRoomDataSourceFactory private constructor(
    private val identificationModule: IdentificationModule,
    private val identificationDao: IdentificationDao
    ) : Factory<IdentificationRoomDataSource> {

    override fun get(): IdentificationRoomDataSource {
        return identificationModule.provideIdentificationRoomDataSource(identificationDao)
    }

    companion object {
        @JvmStatic
        fun create(identificationModule: IdentificationModule, identificationDao: IdentificationDao): IdentificationRoomDataSourceFactory {
            return IdentificationRoomDataSourceFactory(identificationModule, identificationDao)
        }
    }

}