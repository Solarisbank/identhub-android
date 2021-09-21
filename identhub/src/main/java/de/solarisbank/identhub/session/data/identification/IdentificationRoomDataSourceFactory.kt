package de.solarisbank.identhub.session.data.identification

import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.data.dao.IdentificationDao

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