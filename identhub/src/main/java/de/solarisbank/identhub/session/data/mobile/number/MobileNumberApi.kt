package de.solarisbank.identhub.session.data.mobile.number

import de.solarisbank.identhub.domain.data.dto.MobileNumberDto
import io.reactivex.Single
import retrofit2.http.GET

interface MobileNumberApi {
    @GET("/mobile_number")
    fun getMobileNumber(): Single<MobileNumberDto>
}