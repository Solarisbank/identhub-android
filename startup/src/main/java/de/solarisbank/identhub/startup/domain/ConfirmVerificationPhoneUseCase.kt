package de.solarisbank.identhub.startup.domain

import de.solarisbank.identhub.startup.model.TransactionAuthenticationNumber
import de.solarisbank.identhub.startup.model.VerificationPhoneResponse
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class ConfirmVerificationPhoneUseCase(private val verificationPhoneRepository: VerificationPhoneRepository) : SingleUseCase<String, VerificationPhoneResponse>() {
    override fun invoke(confirmToken: String): Single<NavigationalResult<VerificationPhoneResponse>> {
        return verificationPhoneRepository.confirmToken(TransactionAuthenticationNumber(confirmToken))
                .map { NavigationalResult(it) }
                .observeOn(AndroidSchedulers.mainThread())
    }
}
