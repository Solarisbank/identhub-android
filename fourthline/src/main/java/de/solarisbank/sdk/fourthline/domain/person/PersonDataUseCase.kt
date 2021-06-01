package de.solarisbank.sdk.fourthline.domain.person

import android.content.Intent
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.identhub.router.NEXT_STEP_DIRECTION
import de.solarisbank.identhub.router.NEXT_STEP_KEY
import de.solarisbank.identhub.router.isIdentificationIdCreationRequired
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRepository
import io.reactivex.Single
import timber.log.Timber

class PersonDataUseCase(
        private val fourthlineIdentificationRepository: FourthlineIdentificationRepository,
        private val sessionUrlLocalDataSource: SessionUrlLocalDataSource
        ) : SingleUseCase<Intent, PersonDataDto>() {


    /**
     * Obtains identificationId, saves it and obtain personal data
     */
    override fun invoke(intent: Intent): Single<NavigationalResult<PersonDataDto>> {
        var sessionUrl = intent.getStringExtra(IdentHub.SESSION_URL_KEY)
        if (sessionUrl != null && !sessionUrl.endsWith("/", true)) {
            sessionUrl = "$sessionUrl/"
        }

        sessionUrlLocalDataSource.store(sessionUrl)

        return fourthlineIdentificationRepository.getLastSavedLocalIdentification()
                .map {
                    entity -> if (entity.nextStep != null && isIdentificationIdCreationRequired(entity)) {
                        return@map passFourthlineIdentificationCreation().blockingGet()
                    } else {
                        return@map entity.id
                    }
                }
                .onErrorResumeNext { passFourthlineIdentificationCreation() }
                .flatMap { fourthlineIdentificationRepository.getPersonData(it) }
                .map { NavigationalResult<PersonDataDto>(it) }
    }

    //todo remove NEXT_STEP_KEY
    private fun getIdentificationId(intent: Intent): Single<String> {
        return if (intent.hasExtra(NEXT_STEP_KEY) && intent.getStringExtra(NEXT_STEP_KEY) == NEXT_STEP_DIRECTION.BANK_ID_FOURTHLINE.destination) {
            Timber.d("getLastSavedLocalIdentification")
            fourthlineIdentificationRepository.getLastSavedLocalIdentification().map { it.id }
        } else {
            Timber.d("passFourthlineIdentificationCreation")
            passFourthlineIdentificationCreation()
        }
    }

    private fun passFourthlineIdentificationCreation(): Single<String> {

        return fourthlineIdentificationRepository.postFourthlineIdentication()
                .map { identificationDto ->
                    fourthlineIdentificationRepository
                            .save(identificationDto).blockingGet()
                    return@map identificationDto.id
                }
    }

    fun getIdentificationId(): Single<String> {
        return fourthlineIdentificationRepository.getLastSavedLocalIdentification().map { it.id }
    }
}