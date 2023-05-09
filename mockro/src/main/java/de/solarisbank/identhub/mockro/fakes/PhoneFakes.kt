package de.solarisbank.identhub.mockro.fakes

import de.solarisbank.identhub.phone.data.VerificationPhoneNetworkDataSource
import de.solarisbank.identhub.phone.model.TransactionAuthenticationNumber
import de.solarisbank.identhub.phone.model.VerificationPhoneResponse
import io.reactivex.Single

class FakeVerificationPhoneNetworkDataSource: VerificationPhoneNetworkDataSource  {
    override fun postAuthorize(): Single<VerificationPhoneResponse> {
        return Single.just(VerificationPhoneResponse(
            "","", false
        ))
    }

    override fun postConfirm(tan: TransactionAuthenticationNumber): Single<VerificationPhoneResponse> {
        return if (tan.token == "212212") {
            Single.just(
                VerificationPhoneResponse(
                    "", "", true
                )
            )
        } else {
            Single.error(Throwable("Mocking invalid TAN"))
        }
    }
}