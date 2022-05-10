package de.solarisbank.identhub.session.domain

import de.solarisbank.identhub.session.data.repository.IdentHubSessionRepository
import de.solarisbank.identhub.session.feature.navigation.router.FIRST_STEP_KEY
import de.solarisbank.identhub.session.feature.navigation.router.NEXT_STEP_KEY
import de.solarisbank.sdk.data.dto.InitializationDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.TimeUnit

class IdentHubSessionUseCase(
    private val identHubSessionRepository: IdentHubSessionRepository,
    private val sessionUrlRepository: SessionUrlRepository,
    override val identityInitializationRepository: IdentityInitializationRepository
) : NextStepSelector {

    fun saveSessionId(url: String?) {
        sessionUrlRepository.save(url)
    }

    fun getNextStep(): Single<NavigationalResult<String>> {
        return identHubSessionRepository
            .getSavedIdentificationId()
            .map {
                Timber.d("obtainLocalIdentificationState() 1: $it")
                val nextStep = selectNextStep(it.nextStep, it.fallbackStep)
                return@map NavigationalResult(NEXT_STEP_KEY, nextStep)
            }
    }

    fun obtainLocalIdentificationState(): Single<NavigationalResult<String>> {
        Timber.d("obtainLocalIdentificationState()")
        var isRetrieved = false
        val obtainer = object : SingleUseCase<String, InitializationDto>() {
            override fun invoke(param: String): Single<NavigationalResult<InitializationDto>> {
                return identHubSessionRepository
                    .getRequiredIdentificationFlow().map { NavigationalResult(it) }
            }
        }

        return Observable
            .interval(0, 5, TimeUnit.SECONDS)
            .timeout(60, TimeUnit.SECONDS)
            .takeWhile{ !isRetrieved }
            .flatMapSingle {
                return@flatMapSingle obtainer.execute(sessionUrlRepository.get()!!)
                    .map { it -> if (it.succeeded) {
                        Timber.d("obtainLocalIdentificationState() 2 : ${it}")
                      isRetrieved = true
                    }
                    it
                    }
            }
            .toList()
            .map { it.last() }
            .flatMap {
                return@flatMap if (it.succeeded && it.data != null) {
                    identityInitializationRepository.saveInitializationDto(it.data!!)
                    identHubSessionRepository
                        .getSavedIdentificationId()
                        .map {
                            Timber.d("obtainLocalIdentificationState() 1: $it")
                            val nextStep = selectNextStep(it.nextStep, it.fallbackStep)
                                ?: throw IllegalStateException("NextStep is null")
                            NavigationalResult(NEXT_STEP_KEY, nextStep)
                        }
                        .onErrorReturnItem(
                            NavigationalResult(FIRST_STEP_KEY, it.data!!.firstStep)
                        )
                } else {
                    Single.error(IllegalStateException("InitializationDto was not fetched"))
                }
            }
    }

}