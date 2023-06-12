package de.solarisbank.sdk.data.api

import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface IdentificationApi {

    @GET("/identifications/{identification_uid}")
    fun getIdentification(@Path("identification_uid") identificationId: String): Single<IdentificationDto>

    @GET("/identifications/{identification_uid}")
    suspend fun fetchIdentification(@Path("identification_uid") identificationId: String): IdentificationDto

}