package de.solarisbank.identhub.session.data.identification

import de.solarisbank.identhub.data.dto.IdentificationDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface IdentificationApi {

    @GET("/identifications/{identification_uid}")
    fun getIdentificationStatus(@Path("identification_uid") identificationId: String): Single<IdentificationDto>
}