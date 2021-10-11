package de.solarisbank.identhub.verfication.phone

import de.solarisbank.sdk.data.dto.MobileNumberDto
import de.solarisbank.sdk.domain.model.result.Result
import io.reactivex.Completable
import io.reactivex.Single

interface PhoneVerificationUseCase {
    fun verifyToken(token: String): Completable
    fun resendCode(): Completable
    fun fetchPhoneNumber(): Single<Result<MobileNumberDto>>
}