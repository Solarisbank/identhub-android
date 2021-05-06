package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FourthlineIdentificationApi {
    @POST("/fourthline_identification")
    fun postFourthlineIdentication(): Single<IdentificationDto>

    @GET("/identifications/{identification_uid}/person_data")
    fun getPersonData(@Path("identification_uid") identificationId: String): Single<PersonDataDto>

    @GET("/identifications/{identification_uid}")
    fun getIdentifications(@Path("identification_uid") identificationId: String): Single<IdentificationDto>
    
}