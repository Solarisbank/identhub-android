package de.solarisbank.sdk.fourthline.data.ip

import de.solarisbank.sdk.fourthline.data.dto.IpDto
import io.reactivex.Single

class IpDataSource(private val ipApi: IpApi) {
    fun getMyIp(): Single<IpDto> {
        return ipApi.getMyIp()
    }
}