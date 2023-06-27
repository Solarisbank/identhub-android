package de.solarisbank.identhub.startup.feature

import de.solarisbank.identhub.startup.domain.AuthorizeVerificationPhoneUseCase
import de.solarisbank.identhub.startup.domain.ConfirmVerificationPhoneUseCase
import de.solarisbank.sdk.domain.model.result.data
import io.reactivex.Completable

class PhoneVerificationUseCaseImpl(
    private val authorizeVerificationPhoneUseCase: AuthorizeVerificationPhoneUseCase,
    private val confirmVerificationPhoneUseCase: ConfirmVerificationPhoneUseCase,
): PhoneVerificationUseCase {
    override fun verifyToken(token: String): Completable {
        return confirmVerificationPhoneUseCase.execute(token)
            .doOnSuccess {
                if (it.data?.isVerified != true) throw Error("Verification failed")
            }
            .ignoreElement()
    }

    override fun authorize(): Completable {
        return authorizeVerificationPhoneUseCase.execute(Unit)
            .ignoreElement()
    }
}