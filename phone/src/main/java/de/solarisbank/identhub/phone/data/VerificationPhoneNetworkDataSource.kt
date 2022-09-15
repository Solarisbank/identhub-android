package de.solarisbank.identhub.phone.data

import de.solarisbank.identhub.phone.model.TransactionAuthenticationNumber
import de.solarisbank.identhub.phone.model.VerificationPhoneResponse
import io.reactivex.Single

interface VerificationPhoneNetworkDataSource {
    fun postAuthorize(): Single<VerificationPhoneResponse>
    fun postConfirm(tan: TransactionAuthenticationNumber): Single<VerificationPhoneResponse>
}