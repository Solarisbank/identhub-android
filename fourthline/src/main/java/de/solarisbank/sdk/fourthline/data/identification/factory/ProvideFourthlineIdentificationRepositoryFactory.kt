package de.solarisbank.sdk.fourthline.data.identification.factory

import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationModule
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRepository
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.person.PersonDataDataSource

class ProvideFourthlineIdentificationRepositoryFactory private constructor(
    private val fourthlineIdentificationModule: FourthlineIdentificationModule,
    private val fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>,
    private val identificationRoomDataSourceProvider: Provider<out IdentificationLocalDataSource>,
    private val personDataDataSourceProvider: Provider<PersonDataDataSource>
        ) :
    Factory<FourthlineIdentificationRepository> {

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
            identificationRoomDataSourceProvider: Provider<out IdentificationLocalDataSource>,
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