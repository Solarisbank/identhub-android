package de.solarisbank.sdk.fourthline.data.identification.factory

import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationModule
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRoomDataSource

class ProvideFourthlineIdentificationRoomDataSourceFactory private constructor(
        private val fourthlineIdentificationModule: FourthlineIdentificationModule,
        private val identificationDaoProvider: Provider<IdentificationDao>
) : Factory<FourthlineIdentificationRoomDataSource> {

    override fun get(): FourthlineIdentificationRoomDataSource {
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