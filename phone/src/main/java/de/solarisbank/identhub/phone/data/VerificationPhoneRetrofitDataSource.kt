package de.solarisbank.identhub.phone.data

import de.solarisbank.identhub.phone.model.TransactionAuthenticationNumber
import de.solarisbank.identhub.phone.model.VerificationPhoneResponse
import io.reactivex.Single

class VerificationPhoneRetrofitDataSource(private val verificationPhoneApi: VerificationPhoneApi) :
    VerificationPhoneNetworkDataSource {
    override fun postAuthorize(): Single<VerificationPhoneResponse> {
        return verificationPhoneApi.postAuthorize()
    }

    override fun postConfirm(tan: TransactionAuthenticationNumber): Single<VerificationPhoneResponse> {
        return verificationPhoneApi.postConfirm(tan)
    }
}