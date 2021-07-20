package de.solarisbank.identhub.data.initialization

import de.solarisbank.identhub.domain.data.dto.InitializationDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface InitializeIdentificationApi {
    @GET
    fun getInitialization(@Url url: String): Single<InitializationDto>
}