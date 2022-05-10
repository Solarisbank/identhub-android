package de.solarisbank.identhub.session.data.network

import de.solarisbank.sdk.data.dto.InitializationDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface InitializeIdentificationApi { //Url added in DynamicBaseUrlInterceptor
    @GET(".")
    fun getInitialization(): Single<InitializationDto>
}