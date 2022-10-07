package de.solarisbank.sdk.fourthline.data.ip

import de.solarisbank.sdk.fourthline.data.dto.IpDto
import io.reactivex.Single
import retrofit2.http.GET

interface IpApi {

    @GET("/myip")
    fun getMyIp(): Single<IpDto>
}