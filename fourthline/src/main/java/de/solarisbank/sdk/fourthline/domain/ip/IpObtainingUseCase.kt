package de.solarisbank.sdk.fourthline.domain.ip

import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import de.solarisbank.sdk.fourthline.data.ip.IpRepository
import io.reactivex.Single

class IpObtainingUseCase(private val ipRepository: IpRepository) : SingleUseCase<Unit, String>() {
    override fun invoke(param: Unit): Single<NavigationalResult<String>> {
        return  ipRepository.getMyIp().map { NavigationalResult(it.ip) }
    }
}