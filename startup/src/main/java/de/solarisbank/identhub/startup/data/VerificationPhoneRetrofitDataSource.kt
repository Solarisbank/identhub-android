package de.solarisbank.identhub.startup.data

import de.solarisbank.identhub.startup.model.TransactionAuthenticationNumber
import de.solarisbank.identhub.startup.model.VerificationPhoneResponse
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