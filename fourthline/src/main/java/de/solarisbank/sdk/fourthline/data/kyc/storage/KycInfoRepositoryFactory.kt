package de.solarisbank.sdk.fourthline.data.kyc.storage

import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class KycInfoRepositoryFactory private constructor(
        private val kycInfoInMemoryDataSourceProvider: Provider<KycInfoInMemoryDataSource>
        ): Factory<KycInfoRepository> {

    override fun get(): KycInfoRepository {
        return KycInfoRepository(kycInfoInMemoryDataSourceProvider.get())
    }

    companion object {
        @JvmStatic
        fun create(
                kycInfoInMemoryDataSourceProvider: Provider<KycInfoInMemoryDataSource>
        ): KycInfoRepositoryFactory {
            return KycInfoRepositoryFactory(kycInfoInMemoryDataSourceProvider)
        }
    }
}