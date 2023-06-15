package de.solarisbank.sdk.fourthline.domain.ip

import de.solarisbank.sdk.fourthline.data.dto.IpDto
import de.solarisbank.sdk.fourthline.data.ip.IpApi

interface IpObtainingUseCase {
    suspend fun getMyIp(): IpDto
}

class IpObtainingUseCaseImpl(private val ipApi: IpApi) : IpObtainingUseCase {
    override suspend fun getMyIp(): IpDto {
        return ipApi.getMyIp()
    }
}