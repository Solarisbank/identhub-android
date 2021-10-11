package de.solarisbank.identhub.verfication.phone

import de.solarisbank.identhub.domain.contract.GetMobileNumberUseCase
import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCase
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCase
import de.solarisbank.sdk.data.dto.MobileNumberDto
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.data
import io.reactivex.Completable
import io.reactivex.Single

class PhoneVerificationUseCaseImpl(
    private val authorizeVerificationPhoneUseCase: AuthorizeVerificationPhoneUseCase,
    private val confirmVerificationPhoneUseCase: ConfirmVerificationPhoneUseCase,
    private val getMobileNumberUseCase: GetMobileNumberUseCase
): PhoneVerificationUseCase {
    override fun verifyToken(token: String): Completable {
        return confirmVerificationPhoneUseCase.execute(token)
            .doOnSuccess {
                if (it.data?.isVerified != true) throw Error("Verification failed")
            }
            .ignoreElement()
    }

    override fun resendCode(): Completable {
        return authorizeVerificationPhoneUseCase.execute(Unit)
            .ignoreElement()
    }

    override fun fetchPhoneNumber(): Single<Result<MobileNumberDto>> {
        return getMobileNumberUseCase.execute(Unit)
    }
}