package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import io.reactivex.Single
import retrofit2.http.POST

interface FourthlineIdentificationApi {

    @POST("/fourthline_identification")
    fun postFourthlineIdentication(): Single<IdentificationDto>
    
}