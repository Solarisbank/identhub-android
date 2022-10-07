package de.solarisbank.sdk.fourthline.data.ip

import de.solarisbank.sdk.fourthline.data.dto.IpDto
import io.reactivex.Single

class IpRepository(private val ipDataSource: IpDataSource) {

    fun getMyIp(): Single<IpDto> {
        return ipDataSource.getMyIp()
    }

}