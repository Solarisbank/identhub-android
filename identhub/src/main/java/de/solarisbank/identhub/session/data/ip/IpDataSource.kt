package de.solarisbank.identhub.session.data.ip

import de.solarisbank.identhub.domain.data.dto.IpDto
import io.reactivex.Single

class IpDataSource(private val ipApi: IpApi) {
    fun getMyIp(): Single<IpDto> {
        return ipApi.getMyIp()
    }
}