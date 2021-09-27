package de.solarisbank.identhub.session.data.ip

import de.solarisbank.identhub.domain.data.dto.IpDto
import io.reactivex.Single

class IpRepository(private val ipDataSource: IpDataSource) {

    fun getMyIp(): Single<IpDto> {
        return ipDataSource.getMyIp()
    }

}