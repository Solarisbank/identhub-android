package de.solarisbank.identhub.session.data.identification

import de.solarisbank.sdk.core.di.internal.Factory

class IdentificationRepositoryFactory private constructor(
        private val identificationRoomDataSource: IdentificationRoomDataSource,
        private val identificationRetrofitDataSource: IdentificationRetrofitDataSource
        ) : Factory<IdentificationRepository>{

    override fun get(): IdentificationRepository {
        return IdentificationRepository(identificationRoomDataSource, identificationRetrofitDataSource)
    }

    companion object {
        @JvmStatic
        fun create(identificationRoomDataSource: IdentificationRoomDataSource, identificationRetrofitDataSource: IdentificationRetrofitDataSource): IdentificationRepositoryFactory {
            return IdentificationRepositoryFactory(identificationRoomDataSource, identificationRetrofitDataSource)
        }
    }
}