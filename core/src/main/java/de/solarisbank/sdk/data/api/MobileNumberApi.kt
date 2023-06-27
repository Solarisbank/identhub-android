package de.solarisbank.sdk.data.api

import de.solarisbank.sdk.data.dto.MobileNumberDto
import retrofit2.http.GET

interface MobileNumberApi {
    @GET("/mobile_number")
    suspend fun getMobileNumber(): MobileNumberDto
}