package de.solarisbank.sdk.fourthline.data.identification.factory

import de.solarisbank.sdk.data.dao.IdentificationDao
import de.solarisbank.sdk.data.datasource.IdentificationRoomDataSource
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationModule

class ProvideFourthlineIdentificationRoomDataSourceFactory private constructor(
        private val fourthlineIdentificationModule: FourthlineIdentificationModule,
        private val identificationDaoProvider: Provider<IdentificationDao>
) : Factory<IdentificationRoomDataSource> {

    override fun get(): IdentificationRoomDataSource {
        return fourthlineIdentificationModule.provideFourthlineIdentificationRoomDataSource(
                identificationDaoProvider.get()
        )
    }

    companion object {
        @JvmStatic
        fun create(
                fourthlineIdentificationModule: FourthlineIdentificationModule,
                identificationDaoProvider: Provider<IdentificationDao>
        ) : ProvideFourthlineIdentificationRoomDataSourceFactory{
            return ProvideFourthlineIdentificationRoomDataSourceFactory(
                    fourthlineIdentificationModule,
                    identificationDaoProvider
            )
        }
    }
}