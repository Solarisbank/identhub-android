package de.solarisbank.identhub.session.domain

import android.content.Context
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.identhub.domain.session.NextStepSelector
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.router.FIRST_STEP_KEY
import de.solarisbank.identhub.router.NEXT_STEP_KEY
import de.solarisbank.identhub.session.data.IdentHubSessionRepository
import de.solarisbank.identhub.session.utils.isServiceRunning
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

    fun obtainLocalIdentificationState(): Single<NavigationalResult<String>> {
        Timber.d("obtainLocalIdentificationState()")
        return Single.just(isServiceRunning(context))
                .flatMap { isServiceRunning ->
                    if (isServiceRunning) {
                        //todo implement uploading status saving
                        Single.just(NavigationalResult("IdentHubSessionViewModel.LOCAL_IDENTIFICATION_STATE.PROCESSING", "upload_screen"))
                    } else {
                        Timber.d("obtainLocalIdentificationState() 0")
                        identHubSessionRepository
                                .getSavedIdentificationId()
                                .map {
                                    Timber.d("obtainLocalIdentificationState() 1: $it")
                                    val nextStep = selectNextStep(it.nextStep, it.fallbackStep)
                                    return@map NavigationalResult(NEXT_STEP_KEY, nextStep)
                                }
                                .doOnError{
                                    Timber.e(it,"doOnError")
                                }
                                .onErrorReturnItem(
                                        identHubSessionRepository.getRequiredIdentificationFlow(sessionUrlRepository.get()!!)
                                                .map {
                                                    Timber.d("obtainLocalIdentificationState() 3")
                                                    identityInitializationRepository.saveInitializationDto(it)
                                                    val result = NavigationalResult(FIRST_STEP_KEY, it.firstStep)
                                                    Timber.d("obtainLocalIdentificationState() 4, $result")
                                                    return@map result
                                                }.blockingGet()
                                )
                    }
                }

    }

}