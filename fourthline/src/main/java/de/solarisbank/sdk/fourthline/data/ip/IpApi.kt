package de.solarisbank.sdk.fourthline.data.ip

import de.solarisbank.sdk.fourthline.data.dto.IpDto
import retrofit2.http.GET

interface IpApi {

    @GET("/myip")
    suspend fun getMyIp(): IpDto
}