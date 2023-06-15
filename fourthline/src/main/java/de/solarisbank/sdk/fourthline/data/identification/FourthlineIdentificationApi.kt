package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.sdk.data.dto.IdentificationDto
import retrofit2.http.POST

interface FourthlineIdentificationApi {

    @POST("/fourthline_identification")
    suspend fun createFourthlineIdentification(): IdentificationDto

    @POST("/fourthline_signing")
    suspend fun createFourthlineSigningIdentification(): IdentificationDto

}