package de.solarisbank.identhub.data.ip

import de.solarisbank.identhub.domain.data.dto.IpDto
import io.reactivex.Single
import retrofit2.http.GET

interface IpApi {

    @GET("/myip")
    fun getMyIp(): Single<IpDto>
}