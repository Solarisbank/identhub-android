package de.solarisbank.identhub.phone.data

import de.solarisbank.identhub.phone.domain.VerificationPhoneRepository
import de.solarisbank.identhub.phone.model.TransactionAuthenticationNumber
import de.solarisbank.identhub.phone.model.VerificationPhoneResponse
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