package de.solarisbank.sdk.fourthline.data.person

import de.solarisbank.sdk.data.dto.PersonDataDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PersonDataApi {

    @GET("/identifications/{identification_uid}/person_data")
    suspend fun getPersonData(@Path("identification_uid") identificationId: String): PersonDataDto

}