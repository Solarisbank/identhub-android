package de.solarisbank.identhub.session.domain

import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.entity.Status
import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.identhub.session.data.identification.IdentificationRepository
import de.solarisbank.sdk.core.data.model.IdentificationUiModel
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.TimeUnit

class IdentificationPollingStatusUseCase(private val identificationRepository: IdentificationRepository) : SingleUseCase<Unit, IdentificationUiModel>() {
    //todo should return IdentificationDto. Convertion to IdentificationUiModel should be in viewmodel
    override fun invoke(param: Unit): Single<NavigationalResult<IdentificationUiModel>> {
        return pollIdentificationStatus().map { convertToNavigationalResult(it) }
    }

    fun convertToNavigationalResult(identification: IdentificationDto): NavigationalResult<IdentificationUiModel> {
        return NavigationalResult(IdentificationUiModel(
            id = identification.id,
            status = identification.status,
            failureReason = identification.failureReason,
            nextStep = identification.nextStep),
            identification.nextStep
        )
    }

    fun pollIdentificationStatus(): Single<IdentificationDto> {
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
                            .doOnSuccess { identification -> identification.nextStep?.let {
                                identificationRepository.insertIdentification(Identification(
                                        id = identification.id,
                                        status = identification.status,
                                        nextStep = it,
                                        url = identification.url ?: "", //todo make it nullable in db
                                        method = identification.method)
                                ).blockingGet()
                            }
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