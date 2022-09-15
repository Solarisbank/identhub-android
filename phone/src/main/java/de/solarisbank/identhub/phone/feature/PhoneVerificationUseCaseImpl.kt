package de.solarisbank.identhub.phone.feature

import de.solarisbank.identhub.phone.domain.GetMobileNumberUseCase
import de.solarisbank.sdk.data.dto.MobileNumberDto
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.data
import io.reactivex.Completable
import io.reactivex.Single

class PhoneVerificationUseCaseImpl(
    private val authorizeVerificationPhoneUseCase: de.solarisbank.identhub.phone.domain.AuthorizeVerificationPhoneUseCase,
    private val confirmVerificationPhoneUseCase: de.solarisbank.identhub.phone.domain.ConfirmVerificationPhoneUseCase,
    private val getMobileNumberUseCase: GetMobileNumberUseCase
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

    override fun fetchPhoneNumber(): Single<Result<MobileNumberDto>> {
        return getMobileNumberUseCase.execute(Unit)
    }
}