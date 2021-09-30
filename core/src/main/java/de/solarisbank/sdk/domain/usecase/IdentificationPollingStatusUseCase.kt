package de.solarisbank.sdk.domain.usecase

import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.domain.NextStepSelector
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.TimeUnit

class IdentificationPollingStatusUseCase(
    private val identificationRepository: IdentificationRepository,
    override val identityInitializationRepository: IdentityInitializationRepository
) : SingleUseCase<Unit, IdentificationDto>(), NextStepSelector {

    override fun invoke(param: Unit): Single<NavigationalResult<IdentificationDto>> {
        return pollIdentificationStatus().map { convertToNavigationalResult(it) }
    }

    fun convertToNavigationalResult(identification: IdentificationDto): NavigationalResult<IdentificationDto> {
        return NavigationalResult(identification)
    }

    fun pollIdentificationStatus(): Single<IdentificationDto> {
        Timber.d("pollIdentificationStatus() 0")
        var count = 0L
        var isResultObtainer = false
        return identificationRepository
                .getStoredIdentification()
                .flatMap { identification ->
                    Observable
                            .interval(0, 5, TimeUnit.SECONDS)
                            .doOnNext {
                                count = it
                            }
                            .takeUntil { count > 60}
                            .timeout(60, TimeUnit.SECONDS)
                            .flatMap {
                                return@flatMap Observable.create<IdentificationDto> { emitter ->
                                    emitter.onNext(identificationRepository.getRemoteIdentificationDto(identification.id).blockingGet())
                                    emitter.onComplete()
                                }
                            }
                            .map { t ->
                                Timber.d("pollIdentificationStatus(), status : ${Status.getEnum(t.status)}")
                                t
                            }
                            .takeWhile { !isResultObtainer }
                            .doOnNext{ isResultObtainer = checkPollingResultCondition(it) }
                            .toList()
                            .map {
                                Timber.d("pollIdentificationStatus(), it.last() ${ it.last().status }")
                                it.last()
                            }
                            .doOnSuccess {
                                identificationRepository
                                        .insertIdentification(it)
                                        .blockingGet()
                            }
                }
    }

    private fun checkPollingResultCondition(dto: IdentificationDto): Boolean {
        Timber.d("checkPollingResultCondition: ${Status.getEnum(dto.status) == Status.IDENTIFICATION_DATA_REQUIRED} ")
        return (
                Status.getEnum(dto.status) == Status.IDENTIFICATION_DATA_REQUIRED)
                        || (Status.getEnum(dto.status) == Status.AUTHORIZATION_REQUIRED)
                        || (Status.getEnum(dto.status) == Status.SUCCESSFUL)
                        || (Status.getEnum(dto.status) == Status.FAILED)
                        || (dto.nextStep != null)
    }
}