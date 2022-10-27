package de.solarisbank.identhub.bank.domain

import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.data.entity.Status.Companion.getEnum
import de.solarisbank.sdk.domain.usecase.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class FetchingAuthorizedIBanStatusUseCase(private val verificationBankRepository: VerificationBankRepository) :
    CompletableUseCase<String> {
    override fun execute(identificationId: String): Completable {
        return Observable
            .interval(3, TimeUnit.SECONDS)
            .flatMapSingle {
                verificationBankRepository.getVerificationStatus(identificationId)
            }
            .takeUntil { isAuthorizationRequiredOrFailed(it) }
            .filter { isAuthorizationRequiredOrFailed(it) }
            .flatMapCompletable { verificationBankRepository.save(it) }
    }

    private fun isAuthorizationRequiredOrFailed(identificationDto: IdentificationDto): Boolean {
        val currentStatus = getEnum(identificationDto.status)
        return Status.AUTHORIZATION_REQUIRED === currentStatus || Status.IDENTIFICATION_DATA_REQUIRED === currentStatus || Status.FAILED === currentStatus
    }
}