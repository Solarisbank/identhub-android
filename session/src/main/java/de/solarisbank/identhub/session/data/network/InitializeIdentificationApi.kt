package de.solarisbank.identhub.session.data.network

import de.solarisbank.sdk.data.dto.InitializationDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface InitializeIdentificationApi {
    @GET
    fun getInitialization(@Url url: String): Single<InitializationDto>
}