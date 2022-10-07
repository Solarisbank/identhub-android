package de.solarisbank.identhub.bank.domain

import de.solarisbank.identhub.bank.data.ProcessingVerificationDto
import de.solarisbank.sdk.data.entity.FailureReason
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.domain.model.PollingParametersDto
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single
import timber.log.Timber

class ProcessingVerificationUseCase(
    private val identificationPollingStatusUseCase: IdentificationPollingStatusUseCase,
    private val bankIdPostUseCase: BankIdPostUseCase,
    override val identityInitializationRepository: IdentityInitializationRepository
) : SingleUseCase<String, ProcessingVerificationDto>(), NextStepSelector {

    override fun invoke(iban: String): Single<NavigationalResult<ProcessingVerificationDto>> {
        Timber.d("invoke")
        return identificationPollingStatusUseCase
                .execute(PollingParametersDto())
                .map {
                    Timber.d("processing identification: $it")
                    var processingVerificationDto: ProcessingVerificationDto? = null
                    val data = it.data
                    val nextStep = selectNextStep(data?.nextStep, data?.fallbackStep)
                    if (it.succeeded && it.data != null && nextStep != null) {
                        Timber.d("processing identification 1, dto: $data")
                        val status = Status.getEnum(data?.status)
                        if (status == Status.FAILED) {
                            Timber.d("processing identification 2")
                            processingVerificationDto = when (FailureReason.getEnum(data?.failureReason)) {
                                FailureReason.ACCESS_BY_AUTHORIZED_HOLDER -> {
                                    Timber.d("processing identification 3")
                                    ProcessingVerificationDto.PaymentInitAuthPersonError(nextStep)
                                }
                                FailureReason.ACCOUNT_SNAPSHOT_FAILED -> {
                                    Timber.d("processing identification 4")
                                    ProcessingVerificationDto.PaymentInitFailed(nextStep)
                                }
                                FailureReason.EXPIRED -> {
                                    Timber.d("processing identification 4")
                                    ProcessingVerificationDto.PaymentInitExpired(nextStep)
                                }
                                FailureReason.JOINT_ACCOUNT-> {
                                    Timber.d("processing identification 5")
                                    val jointResult = bankIdPostUseCase.execute(Pair(iban, data!!)).blockingGet()
                                    if (jointResult.succeeded) {
                                        Timber.d("processing identification 6")
                                        ProcessingVerificationDto.VerificationSuccessful(jointResult.data!!.id, nextStep)
                                    } else {
                                        Timber.d("processing identification 7")
                                        ProcessingVerificationDto.GenericError
                                    }
                                }
                                else -> {
                                    Timber.d("processing identification 8")
                                    ProcessingVerificationDto.GenericError
                                }
                            }
                        } else if (
                                (status == Status.AUTHORIZATION_REQUIRED || status == Status.IDENTIFICATION_DATA_REQUIRED)
                                && data != null
                        ) {
                            Timber.d("processing identification 9")
                            processingVerificationDto = ProcessingVerificationDto.VerificationSuccessful(data.id, nextStep)
                        } else {
                            Timber.d("processing identification 10")
                            processingVerificationDto = ProcessingVerificationDto.GenericError
                        }
                    }

                    if (processingVerificationDto == null) {
                        Timber.d("processing identification 11")
                        processingVerificationDto = ProcessingVerificationDto.GenericError
                    }
                    return@map NavigationalResult(processingVerificationDto)
                }
    }

}