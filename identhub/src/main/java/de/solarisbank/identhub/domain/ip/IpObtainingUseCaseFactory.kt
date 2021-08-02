package de.solarisbank.identhub.domain.ip

import de.solarisbank.identhub.data.ip.IpRepository
import de.solarisbank.sdk.core.di.internal.Factory

class IpObtainingUseCaseFactory(val ipRepository: IpRepository) : Factory<IpObtainingUseCase> {

    override fun get(): IpObtainingUseCase {
        return IpObtainingUseCase(ipRepository)
    }

    companion object {
        fun create(ipRepository: IpRepository): IpObtainingUseCaseFactory {
            return IpObtainingUseCaseFactory(ipRepository)
        }
    }
}