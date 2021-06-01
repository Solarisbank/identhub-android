package de.solarisbank.identhub.session.domain

import android.content.Context
import de.solarisbank.identhub.data.entity.NavigationalResult
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
        private val context: Context
        ) {

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
                                    Timber.d("obtainLocalIdentificationState() 1")
                                    //todo keep, it is mocked
                                    NavigationalResult(NEXT_STEP_KEY, it.nextStep)
//                                    identHubSessionRepository.getRequiredIdentificationFlow(sessionUrlRepository.get()!!)
//                                            .map {
//                                                Timber.d("obtainLocalIdentificationState() 2")
//                                                NavigationalResult(it.firstStep)
//                                            }.blockingGet()
                                }
                                .onErrorReturnItem(

                                        identHubSessionRepository.getRequiredIdentificationFlow(sessionUrlRepository.get()!!)
                                                .map {
                                                    Timber.d("obtainLocalIdentificationState() 3")
                                                    NavigationalResult(FIRST_STEP_KEY, it.firstStep)
                                                }.blockingGet()
                                )
                    }
                }

    }

}