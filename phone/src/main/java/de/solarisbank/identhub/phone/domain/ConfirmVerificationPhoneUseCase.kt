package de.solarisbank.identhub.phone.domain

import de.solarisbank.identhub.phone.model.TransactionAuthenticationNumber
import de.solarisbank.identhub.phone.model.VerificationPhoneResponse
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
