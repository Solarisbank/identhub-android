package de.solarisbank.identhub.session.data.identification

import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface IdentificationApi {

    @GET("/identifications/{identification_uid}")
    fun getIdentification(@Path("identification_uid") identificationId: String): Single<IdentificationDto>

}