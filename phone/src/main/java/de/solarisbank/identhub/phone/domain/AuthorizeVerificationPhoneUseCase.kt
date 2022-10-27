package de.solarisbank.identhub.phone.domain

import de.solarisbank.identhub.phone.model.VerificationPhoneResponse
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class AuthorizeVerificationPhoneUseCase(
    private val verificationPhoneRepository: VerificationPhoneRepository
    ) : SingleUseCase<Unit, VerificationPhoneResponse>() {

    override fun invoke(param: Unit): Single<NavigationalResult<VerificationPhoneResponse>> {
        return verificationPhoneRepository.authorize()
                .map { NavigationalResult(it) }
                .observeOn(AndroidSchedulers.mainThread())
    }
}