package de.solarisbank.sdk.fourthline.data.ip

import de.solarisbank.sdk.fourthline.data.dto.IpDto
import io.reactivex.Single

interface IpDataSource {
    fun getMyIp(): Single<IpDto>
}

class IpDataSourceImpl(private val ipApi: IpApi): IpDataSource {
    override fun getMyIp(): Single<IpDto> {
        return ipApi.getMyIp()
    }
}