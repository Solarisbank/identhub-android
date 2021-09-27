package de.solarisbank.sdk.fourthline.data.kyc.storage

import de.solarisbank.sdk.feature.di.internal.Factory

class KycInfoInMemoryDataSourceFactory :
    Factory<KycInfoInMemoryDataSource> {

    override fun get(): KycInfoInMemoryDataSource {
        return KycInfoInMemoryDataSource()
    }

    companion object {
        fun create(): KycInfoInMemoryDataSourceFactory {
            return KycInfoInMemoryDataSourceFactory()
        }
    }
}