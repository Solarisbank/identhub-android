package de.solarisbank.identhub.session.domain

import android.content.Context
import de.solarisbank.identhub.domain.navigation.router.FIRST_STEP_KEY
import de.solarisbank.identhub.domain.navigation.router.NEXT_STEP_KEY
import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.identhub.domain.session.NextStepSelector
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.session.data.IdentHubSessionRepository
import de.solarisbank.sdk.data.entity.NavigationalResult
import io.reactivex.Single
import timber.log.Timber


//todo add context for service checking
class IdentHubSessionUseCase(
    private val identHubSessionRepository: IdentHubSessionRepository,
    private val sessionUrlRepository: SessionUrlRepository,
    override val identityInitializationRepository: IdentityInitializationRepository,
    private val context: Context
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