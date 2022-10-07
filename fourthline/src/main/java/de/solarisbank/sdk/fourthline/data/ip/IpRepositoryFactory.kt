package de.solarisbank.sdk.fourthline.data.ip

import de.solarisbank.sdk.feature.di.internal.Factory

class IpRepositoryFactory private constructor(
        private val ipDataSource: IpDataSource
) : Factory<IpRepository> {

    override fun get(): IpRepository {
        return IpRepository(ipDataSource)
    }

    companion object {
        fun create(ipDataSource: IpDataSource): IpRepositoryFactory {
            return IpRepositoryFactory(ipDataSource)
        }
    }
}