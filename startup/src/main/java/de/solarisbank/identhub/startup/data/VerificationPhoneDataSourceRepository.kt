package de.solarisbank.identhub.startup.data

import de.solarisbank.identhub.startup.domain.VerificationPhoneRepository
import de.solarisbank.identhub.startup.model.TransactionAuthenticationNumber
import de.solarisbank.identhub.startup.model.VerificationPhoneResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class VerificationPhoneDataSourceRepository(
    private val verificationPhoneNetworkDataSource: VerificationPhoneNetworkDataSource
    ) : VerificationPhoneRepository {
    override fun authorize(): Single<VerificationPhoneResponse> {
        return verificationPhoneNetworkDataSource.postAuthorize()
            .subscribeOn(Schedulers.io())
    }

    override fun confirmToken(token: TransactionAuthenticationNumber): Single<VerificationPhoneResponse> {
        return verificationPhoneNetworkDataSource.postConfirm(token)
            .subscribeOn(Schedulers.io())
    }
}