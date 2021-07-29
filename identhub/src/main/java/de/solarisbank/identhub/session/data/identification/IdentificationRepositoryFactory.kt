package de.solarisbank.identhub.session.data.identification

import de.solarisbank.identhub.session.data.mobile.number.MobileNumberDataSource
import de.solarisbank.sdk.core.di.internal.Factory

class IdentificationRepositoryFactory private constructor(
        private val identificationRoomDataSource: IdentificationRoomDataSource,
        private val identificationRetrofitDataSource: IdentificationRetrofitDataSource,
        private val mobileNumberDataSource: MobileNumberDataSource
        ) : Factory<IdentificationRepository>{

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
                identificationRoomDataSource: IdentificationRoomDataSource,
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