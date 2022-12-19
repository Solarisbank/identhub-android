package de.solarisbank.sdk.domain.usecase

import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.domain.model.PollingParametersDto
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.TimeUnit

class IdentificationPollingStatusUseCase(
    private val identificationRepository: IdentificationRepository,
    override val initialConfigStorage: InitialConfigStorage
) : SingleUseCase<PollingParametersDto, IdentificationDto>(), NextStepSelector {

    override fun invoke(param: PollingParametersDto): Single<NavigationalResult<IdentificationDto>> {
        return pollIdentificationStatus(param).map { convertToNavigationalResult(it) }
    }

    fun convertToNavigationalResult(identification: IdentificationDto): NavigationalResult<IdentificationDto> {
        return NavigationalResult(identification)
    }

    fun pollIdentificationStatus(pollingParametersDto: PollingParametersDto): Single<IdentificationDto> {
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
                                return@flatMap Observable.create { emitter ->
                                    emitter.onNext(identificationRepository.getRemoteIdentificationDto(identification.id).blockingGet())
                                    emitter.onComplete()
                                }
                            }
                            .map { t ->
                                Timber.d("pollIdentificationStatus(), status : ${Status.getEnum(t.status)}")
                                t
                            }
                            .takeWhile { !isResultObtainer }
                            .doOnNext{ isResultObtainer = checkPollingResultCondition(it, pollingParametersDto.isConfirmedAcceptable) }
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

    private fun checkPollingResultCondition(dto: IdentificationDto, isConfirmedAcceptable: Boolean): Boolean {
        Timber.d("checkPollingResultCondition: ${Status.getEnum(dto.status) == Status.IDENTIFICATION_DATA_REQUIRED} ")
        val currentStatus = Status.getEnum(dto.status)
        return (
                 currentStatus == Status.IDENTIFICATION_DATA_REQUIRED)
                        || (currentStatus == Status.AUTHORIZATION_REQUIRED)
                        || (currentStatus == Status.SUCCESSFUL)
                        || (currentStatus == Status.FAILED)
                        || (dto.nextStep != null)
                || (isConfirmedAcceptable && currentStatus == Status.CONFIRMED)
    }
}