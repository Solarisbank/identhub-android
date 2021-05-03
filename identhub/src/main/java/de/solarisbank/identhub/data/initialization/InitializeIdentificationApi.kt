package de.solarisbank.identhub.data.initialization

import de.solarisbank.identhub.data.dto.InitializationDto
import io.reactivex.Single
import retrofit2.http.GET

interface InitializeIdentificationApi {
    @GET(".")
    fun getInitialization(): Single<InitializationDto>
}