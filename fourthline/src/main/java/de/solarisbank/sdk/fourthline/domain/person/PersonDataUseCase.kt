package de.solarisbank.sdk.fourthline.domain.person

import android.content.Intent
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRepository
import io.reactivex.Single

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

        return fourthlineIdentificationRepository
                .postFourthlineIdentication()
                .flatMap { identificationDto ->
                    fourthlineIdentificationRepository
                            .save(identificationDto)
                            .andThen(
                                    fourthlineIdentificationRepository.getPersonData(identificationDto.id)
                                            .map { it -> NavigationalResult<PersonDataDto>(it) }
                            )
                }
    }

    fun getIdentificationId(): Single<String> {
        return fourthlineIdentificationRepository.getLastSavedLocalIdentification().map { it.id }
    }
}