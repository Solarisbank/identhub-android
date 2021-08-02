package de.solarisbank.identhub.domain.ip

import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.ip.IpRepository
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import io.reactivex.Single

class IpObtainingUseCase(private val ipRepository: IpRepository) : SingleUseCase<Unit, String>() {
    override fun invoke(param: Unit): Single<NavigationalResult<String>> {
        return  ipRepository.getMyIp().map { NavigationalResult(it.ip) }
    }
}