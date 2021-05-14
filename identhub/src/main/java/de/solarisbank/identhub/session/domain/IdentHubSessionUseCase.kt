package de.solarisbank.identhub.session.domain

import android.content.Context
import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.session.data.IdentHubSessionRepository
import de.solarisbank.identhub.session.feature.IdentHubSessionViewModel
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

    fun obtainLocalIdentificationState(): Single<Pair<IdentHubSessionViewModel.LOCAL_IDENTIFICATION_STATE, IdentificationDto?>> {
        Timber.d("obtainLocalIdentificationState()")
        return Single.just(isServiceRunning(context))
                .flatMap { isServiceRunning ->
                    if (isServiceRunning) {
                        Single.just(Pair<IdentHubSessionViewModel.LOCAL_IDENTIFICATION_STATE, IdentificationDto?>(IdentHubSessionViewModel.LOCAL_IDENTIFICATION_STATE.PROCESSING, null))
                    } else {
                        Timber.d("obtainLocalIdentificationState() 0")
                        identHubSessionRepository
                                .getSavedIdentificationId()
                                .map {
                                    Timber.d("obtainLocalIdentificationState() 1")
                                    //todo keep, it is mocked
//                                    identificationDto ->
//                                    Pair<IdentHubSessionViewModel.LOCAL_IDENTIFICATION_STATE, IdentificationDto?>(
//                                            IdentHubSessionViewModel.LOCAL_IDENTIFICATION_STATE.OBTAINED, identificationDto
//                                    )
                                    identHubSessionRepository.getRequiredIdentificationFlow(sessionUrlRepository.get()!!)
                                            .map {
                                                Timber.d("obtainLocalIdentificationState() 2")
                                                Pair(
                                                        //todo replace with converter
                                                        IdentHubSessionViewModel.LOCAL_IDENTIFICATION_STATE.ABSENT,
                                                        IdentificationDto("", nextStep = it.firstStep, status = "", url = null, documents = null)
                                                )
                                            }.blockingGet()
                                }
                                .defaultIfEmpty(

                                        identHubSessionRepository.getRequiredIdentificationFlow(sessionUrlRepository.get()!!)
                                                .map {
                                                    Timber.d("obtainLocalIdentificationState() 3")
                                                    Pair(
                                                            //todo replace with converter
                                                            IdentHubSessionViewModel.LOCAL_IDENTIFICATION_STATE.ABSENT,
                                                            IdentificationDto("", nextStep = it.firstStep, status = "", url = null, documents = null)
                                                    )
                                                }.blockingGet()
                                )
                                .toSingle()
                    }
                }

    }

}