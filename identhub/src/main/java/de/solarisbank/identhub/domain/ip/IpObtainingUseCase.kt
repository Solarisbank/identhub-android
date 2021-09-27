package de.solarisbank.identhub.domain.ip

import de.solarisbank.identhub.session.data.ip.IpRepository
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single

class IpObtainingUseCase(private val ipRepository: IpRepository) : SingleUseCase<Unit, String>() {
    override fun invoke(param: Unit): Single<NavigationalResult<String>> {
        return  ipRepository.getMyIp().map { NavigationalResult(it.ip) }
    }
}