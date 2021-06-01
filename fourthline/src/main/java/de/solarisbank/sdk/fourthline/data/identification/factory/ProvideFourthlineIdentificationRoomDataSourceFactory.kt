package de.solarisbank.sdk.fourthline.data.identification.factory

import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.session.data.identification.IdentificationRoomDataSource
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider
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