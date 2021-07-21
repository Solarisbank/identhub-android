package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.data.entity.FailureReason
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.entity.Status
import de.solarisbank.identhub.domain.data.dto.ProcessingVerificationDto
import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.identhub.domain.session.NextStepSelector
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import io.reactivex.Single
import timber.log.Timber

class ProcessingVerificationUseCase(
    private val identificationPollingStatusUseCase: IdentificationPollingStatusUseCase,
    private val jointAccountBankIdPostUseCase: JointAccountBankIdPostUseCase,
    override val identityInitializationRepository: IdentityInitializationRepository
) : SingleUseCase<String, ProcessingVerificationDto>(), NextStepSelector {

    override fun invoke(iban: String): Single<NavigationalResult<ProcessingVerificationDto>> {
        Timber.d("invoke")
        return identificationPollingStatusUseCase
                .execute(Unit)
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
                                    val jointResult = jointAccountBankIdPostUseCase.execute(Pair(iban, data!!)).blockingGet()
                                    if (jointResult.succeeded) {
                                        Timber.d("processing identification 6")
                                        ProcessingVerificationDto.VerificationSuccessful(jointResult.data!!.id)
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
                            processingVerificationDto = ProcessingVerificationDto.VerificationSuccessful(data.id)
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