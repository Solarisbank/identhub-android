package de.solarisbank.sdk.data.repository

import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.feature.di.internal.Factory

class IdentificationRepositoryFactory private constructor(
    private val identificationRoomDataSource: IdentificationLocalDataSource,
    private val identificationRetrofitDataSource: IdentificationRetrofitDataSource,
    private val mobileNumberDataSource: MobileNumberDataSource
        ) :
    Factory<IdentificationRepository> {

    override fun get(): IdentificationRepository {
        return IdentificationRepository(
                identificationRoomDataSource,
                identificationRetrofitDataSource,
                mobileNumberDataSource
        )
    }

    companion object {
        @JvmStatic
        fun create(
            identificationRoomDataSource: IdentificationLocalDataSource,
            identificationRetrofitDataSource: IdentificationRetrofitDataSource,
            mobileNumberDataSource: MobileNumberDataSource
        ): IdentificationRepositoryFactory {
            return IdentificationRepositoryFactory(
                    identificationRoomDataSource,
                    identificationRetrofitDataSource,
                    mobileNumberDataSource
            )
        }
    }
}