package de.solarisbank.identhub.startup.data

import de.solarisbank.identhub.startup.model.TransactionAuthenticationNumber
import de.solarisbank.identhub.startup.model.VerificationPhoneResponse
import io.reactivex.Single

interface VerificationPhoneNetworkDataSource {
    fun postAuthorize(): Single<VerificationPhoneResponse>
    fun postConfirm(tan: TransactionAuthenticationNumber): Single<VerificationPhoneResponse>
}