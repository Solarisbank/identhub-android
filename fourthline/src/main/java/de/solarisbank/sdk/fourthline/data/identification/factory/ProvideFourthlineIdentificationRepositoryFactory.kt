package de.solarisbank.sdk.fourthline.data.identification.factory

import de.solarisbank.identhub.data.person.PersonDataDataSource
import de.solarisbank.identhub.session.data.identification.IdentificationRoomDataSource
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationModule
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRepository
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource

class ProvideFourthlineIdentificationRepositoryFactory private constructor(
        private val fourthlineIdentificationModule: FourthlineIdentificationModule,
        private val fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>,
        private val identificationRoomDataSourceProvider: Provider<IdentificationRoomDataSource>,
        private val personDataDataSourceProvider: Provider<PersonDataDataSource>
        ) : Factory<FourthlineIdentificationRepository> {

    override fun get(): FourthlineIdentificationRepository {
        return fourthlineIdentificationModule.provideFourthlineIdentificationRepository(
                fourthlineIdentificationRetrofitDataSourceProvider.get(),
                identificationRoomDataSourceProvider.get(),
                personDataDataSourceProvider.get()
        )
    }

    companion object {
        fun create(
                fourthlineIdentificationModule: FourthlineIdentificationModule,
                fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>,
                identificationRoomDataSourceProvider: Provider<IdentificationRoomDataSource>,
                personDataDataSourceProvider: Provider<PersonDataDataSource>
        ) : ProvideFourthlineIdentificationRepositoryFactory {
            return ProvideFourthlineIdentificationRepositoryFactory(
                    fourthlineIdentificationModule,
                    fourthlineIdentificationRetrofitDataSourceProvider,
                    identificationRoomDataSourceProvider,
                    personDataDataSourceProvider
            )
        }
    }
}