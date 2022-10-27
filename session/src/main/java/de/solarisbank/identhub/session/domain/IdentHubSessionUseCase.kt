package de.solarisbank.identhub.session.domain

import android.annotation.SuppressLint
import de.solarisbank.identhub.session.data.dto.SessionStateDto
import de.solarisbank.identhub.session.data.repository.IdentHubSessionRepository
import de.solarisbank.identhub.session.data.repository.SessionStateRepository
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.domain.NextStepSelector
import io.reactivex.Completable
import io.reactivex.Single

class IdentHubSessionUseCase(
    private val identHubSessionRepository: IdentHubSessionRepository,
    private val identHubSessionStateRepository: SessionStateRepository,
    private val sessionUrlRepository: SessionUrlRepository,
    override val initialConfigStorage: InitialConfigStorage
) : NextStepSelector {

    fun saveSessionId(url: String?) {
        sessionUrlRepository.save(url)
    }

    private fun obtainPresavedSessionStateDto(): Single<SessionStateDto> {
        return identHubSessionStateRepository.getSessionStateDto()
    }

    @SuppressLint("CheckResult")
    fun startIdentification(isResumeExpected: Boolean): Single<NavigationalResult<String>> {
        return Single.just(NavigationalResult(""))
//        return obtainPresavedSessionStateDto().flatMap {
//            return@flatMap if (it.started) {
//                Single.error(SessionAlreadyStartedException())
//            } else {
//                Single.zip(
//                    obtainLocalIdentificationState(),
//                    initializationInfoRepository.obtainInfo()
//                ) { navigationResult, _ -> navigationResult }
//            }
//        }
//            .doOnSuccess {
//                if (isResumeExpected) {
//                    identHubSessionStateRepository.saveSessionStateDto(
//                        started = true,
//                        lastNestStep = it.nextStep
//                    ).blockingGet()
//                } else {
//                    identHubSessionStateRepository.saveSessionStateDto(
//                        started = true,
//                        resumed = true,
//                        lastNestStep = it.nextStep
//                    ).blockingGet()
//                }
//            }
    }

    @SuppressLint("CheckResult")
    fun resumeIdentification(): Single<NavigationalResult<String>> {
        return Single.just(NavigationalResult(""))
//        return obtainPresavedSessionStateDto().flatMap {
//            return@flatMap if (it.resumed) {
//                Single.error(SessionAlreadyResumedException())
//            } else {
//                Single.zip(
//                    obtainLocalIdentificationState(),
//                    initializationInfoRepository.obtainInfo()
//                ) { navigationResult, _ -> navigationResult }
//            }
//        }
//            .doOnSuccess {
//                    identHubSessionStateRepository.saveSessionStateDto(
//                        started = true,
//                        resumed = true,
//                        lastNestStep = it.nextStep
//                    ).blockingGet()
//            }
    }

    fun resetIdentification(): Completable {
        return identHubSessionStateRepository.deleteSessionStateDto()
    }

    fun obtainLocalIdentificationState(): Single<NavigationalResult<String>> {
//        Timber.d("obtainLocalIdentificationState()")
//        var isRetrieved = false
//        val obtainer = object : SingleUseCase<Unit, InitializationDto>() {
//            override fun invoke(param: Unit): Single<NavigationalResult<InitializationDto>> {
//                return identHubSessionRepository
//                    .getRequiredIdentificationFlow().map { NavigationalResult(it) }
//            }
//        }
//
//        return Observable
//            .interval(0, 5, TimeUnit.SECONDS)
//            .timeout(60, TimeUnit.SECONDS)
//            .takeWhile{ !isRetrieved }
//            .flatMapSingle {
//                return@flatMapSingle obtainer.execute(Unit)
//                    .map {
//                        if (it.succeeded) {
//                        Timber.d("obtainLocalIdentificationState() 1 : ${it}")
//                      isRetrieved = true
//                    }
//                    it
//                    }
//            }
//            .toList()
//            .map { it.last() }
//            .flatMap {
//                return@flatMap if (it.succeeded && it.data != null) {
//                    identityInitializationRepository.saveInitializationDto(it.data!!)
//                    identHubSessionRepository
//                        .getSavedIdentificationId()
//                        .map {
//                            Timber.d("obtainLocalIdentificationState() 1: $it")
//                            val nextStep = selectNextStep(it.nextStep, it.fallbackStep)
//                                ?: throw IllegalStateException("NextStep is null")
//                            NavigationalResult(NEXT_STEP_KEY, nextStep)
//                        }
//                        .onErrorReturnItem(
//                            NavigationalResult(FIRST_STEP_KEY, it.data!!.firstStep)
//                        )
//                } else {
//                    Single.error(IllegalStateException("InitializationDto was not fetched"))
//                }
//            }
        return Single.just(NavigationalResult(""))
    }
}