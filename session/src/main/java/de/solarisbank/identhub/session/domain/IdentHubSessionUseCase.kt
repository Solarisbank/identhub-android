package de.solarisbank.identhub.session.domain

import de.solarisbank.identhub.session.data.repository.IdentHubSessionRepository
import de.solarisbank.identhub.session.feature.navigation.router.FIRST_STEP_KEY
import de.solarisbank.identhub.session.feature.navigation.router.NEXT_STEP_KEY
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.domain.NextStepSelector
import io.reactivex.Single
import timber.log.Timber

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
        return identHubSessionRepository
            .getRequiredIdentificationFlow(sessionUrlRepository.get()!!)
            .flatMap { initializationDto ->
                identityInitializationRepository.saveInitializationDto(initializationDto)
                identHubSessionRepository
                    .getSavedIdentificationId()
                    .map {
                        Timber.d("obtainLocalIdentificationState() 1: $it")
                        val nextStep = selectNextStep(it.nextStep, it.fallbackStep)
                        NavigationalResult(NEXT_STEP_KEY, nextStep)
                    }
                    .onErrorReturnItem(
                        NavigationalResult(FIRST_STEP_KEY, initializationDto.firstStep)
                    )
            }
    }

}