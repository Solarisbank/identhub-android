package de.solarisbank.identhub.phone.data

import de.solarisbank.identhub.phone.model.TransactionAuthenticationNumber
import de.solarisbank.identhub.phone.model.VerificationPhoneResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface VerificationPhoneApi {
    @POST("mobile_number/authorize")
    fun postAuthorize(): Single<VerificationPhoneResponse>

    @POST("mobile_number/confirm")
    fun postConfirm(@Body tan: TransactionAuthenticationNumber): Single<VerificationPhoneResponse>
}