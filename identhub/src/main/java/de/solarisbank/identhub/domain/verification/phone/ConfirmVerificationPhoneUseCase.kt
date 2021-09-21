package de.solarisbank.identhub.domain.verification.phone

import de.solarisbank.identhub.data.TransactionAuthenticationNumber
import de.solarisbank.identhub.data.verification.phone.model.VerificationPhoneResponse
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.sdk.data.entity.NavigationalResult
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class ConfirmVerificationPhoneUseCase(private val verificationPhoneRepository: VerificationPhoneRepository) : SingleUseCase<String, VerificationPhoneResponse>() {
    override fun invoke(confirmToken: String): Single<NavigationalResult<VerificationPhoneResponse>> {
        return verificationPhoneRepository.confirmToken(TransactionAuthenticationNumber(confirmToken))
                .map { NavigationalResult(it) }
                .observeOn(AndroidSchedulers.mainThread())
    }
}
