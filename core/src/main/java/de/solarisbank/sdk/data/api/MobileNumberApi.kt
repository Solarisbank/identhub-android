package de.solarisbank.sdk.data.api

import de.solarisbank.sdk.data.dto.MobileNumberDto
import io.reactivex.Single
import retrofit2.http.GET

interface MobileNumberApi {
    @GET("/mobile_number")
    fun getMobileNumber(): Single<MobileNumberDto>
}