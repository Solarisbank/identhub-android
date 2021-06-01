package de.solarisbank.identhub.session.data.identification

import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.sdk.core.di.internal.Factory

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