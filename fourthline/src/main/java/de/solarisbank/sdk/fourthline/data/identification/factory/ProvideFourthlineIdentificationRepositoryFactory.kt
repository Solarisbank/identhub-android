package de.solarisbank.sdk.fourthline.data.identification.factory

import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationModule
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRepository
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRoomDataSource

class ProvideFourthlineIdentificationRepositoryFactory private constructor(
        private val fourthlineIdentificationModule: FourthlineIdentificationModule,
        private val fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>,
        private val fourthlineIdentificationRoomDataSourceProvider: Provider<FourthlineIdentificationRoomDataSource>
        ) : Factory<FourthlineIdentificationRepository> {

    override fun get(): FourthlineIdentificationRepository {
        return fourthlineIdentificationModule.provideFourthlineIdentificationRepository(
                fourthlineIdentificationRetrofitDataSourceProvider.get(),
                fourthlineIdentificationRoomDataSourceProvider.get()
        )
    }

    companion object {
        fun create(
                fourthlineIdentificationModule: FourthlineIdentificationModule,
                fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>,
                fourthlineIdentificationRoomDataSourceProvider: Provider<FourthlineIdentificationRoomDataSource>
        ) : ProvideFourthlineIdentificationRepositoryFactory {
            return ProvideFourthlineIdentificationRepositoryFactory(
                    fourthlineIdentificationModule,
                    fourthlineIdentificationRetrofitDataSourceProvider,
                    fourthlineIdentificationRoomDataSourceProvider
            )
        }
    }
}