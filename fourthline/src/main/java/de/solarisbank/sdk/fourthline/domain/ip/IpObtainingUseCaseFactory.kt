package de.solarisbank.sdk.fourthline.domain.ip

import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.fourthline.data.ip.IpRepository

class IpObtainingUseCaseFactory(val ipRepository: IpRepository) :
    Factory<IpObtainingUseCase> {

    override fun get(): IpObtainingUseCase {
        return IpObtainingUseCase(ipRepository)
    }

    companion object {
        fun create(ipRepository: IpRepository): IpObtainingUseCaseFactory {
            return IpObtainingUseCaseFactory(ipRepository)
        }
    }
}