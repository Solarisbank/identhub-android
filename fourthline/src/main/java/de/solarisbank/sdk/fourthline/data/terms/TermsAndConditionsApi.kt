package de.solarisbank.sdk.fourthline.data.terms

import de.solarisbank.sdk.fourthline.data.dto.AcceptTermsBody
import de.solarisbank.sdk.fourthline.data.dto.TermsAndConditions
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TermsAndConditionsApi {
    @GET("/terms_and_conditions/{entity}/latest")
    suspend fun getTermsAndConditions(
        @Path("entity") entity: String,
        @Query("language") languageCode: String
    ): TermsAndConditions

    @POST("/terms_and_conditions/{entity}/accept")
    suspend fun acceptTerms(
        @Path("entity") entity: String,
        @Body acceptTermsBody: AcceptTermsBody
    )
}