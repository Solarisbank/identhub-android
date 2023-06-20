package de.solarisbank.identhub.startup.domain

import de.solarisbank.identhub.startup.model.VerificationPhoneResponse
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