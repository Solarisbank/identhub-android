package de.solarisbank.sdk.feature.config

import de.solarisbank.sdk.data.dto.InitializationInfoDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface InitializationInfoApi {
    @GET
    fun getInfo(@Url url: String): Single<InitializationInfoDto>
}