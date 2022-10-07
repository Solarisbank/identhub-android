package de.solarisbank.sdk.fourthline.data.ip

import de.solarisbank.sdk.feature.di.internal.Factory

class IpDataSourceFactory private constructor(
        private val api: IpApi
        ) : Factory<IpDataSource> {

    override fun get(): IpDataSource {
        return IpDataSource(api)
    }

    companion object {
        fun create(api: IpApi): IpDataSourceFactory {
            return IpDataSourceFactory(api)
        }
    }
}