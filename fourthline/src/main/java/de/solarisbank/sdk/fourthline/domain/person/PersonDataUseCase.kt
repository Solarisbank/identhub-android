package de.solarisbank.sdk.fourthline.domain.person

import de.solarisbank.identhub.session.feature.navigation.router.isIdentificationIdCreationRequired
import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRepository
import de.solarisbank.sdk.fourthline.data.step.parameters.FourthlineStepParametersRepository
import io.reactivex.Single
import timber.log.Timber

class PersonDataUseCase(
        private val fourthlineIdentificationRepository: FourthlineIdentificationRepository,
        private val sessionUrlLocalDataSource: SessionUrlLocalDataSource,
        private val fourthlineStepParametersRepository: FourthlineStepParametersRepository
) : SingleUseCase<String, PersonDataDto>() {


    /**
     * Obtains identificationId, saves it and obtain personal data
     */
    override fun invoke(sessionUrl: String): Single<NavigationalResult<PersonDataDto>> {
        sessionUrlLocalDataSource.store(sessionUrl.formatSessionUrl())

        return fourthlineIdentificationRepository.getLastSavedLocalIdentification()
            .map { entity ->
                if (!fourthlineStepParametersRepository.getFourthlineStepParameters()!!.isFourthlineSigning) {
                    if (isIdentificationIdCreationRequired(entity)) {
                        return@map passFourthlineIdentificationCreation().blockingGet()
                    } else {
                        return@map entity.id
                    }
                } else {
                    return@map passFourthlineIdentificationCreation().blockingGet()
                }
            }
            .onErrorResumeNext { passFourthlineIdentificationCreation() }
            .flatMap { fourthlineIdentificationRepository.getPersonData(it) }
            .map { NavigationalResult<PersonDataDto>(it) }
    }

    fun String.formatSessionUrl(): String {
        return if (!this.endsWith("/", true)) "$this/" else this
    }

    private fun passFourthlineIdentificationCreation(): Single<String> {
        Timber.d("passFourthlineIdentificationCreation()")
        return if (!fourthlineStepParametersRepository.getFourthlineStepParameters()!!.isFourthlineSigning) {
            fourthlineIdentificationRepository.postFourthlineSimplifiedIdentication()
        } else {
            fourthlineIdentificationRepository.postFourthlineSigningIdentication()
        }
            .map { identificationDto ->
                    Timber.d("passFourthlineIdentificationCreation(), identificationDto: $identificationDto")
                    fourthlineIdentificationRepository
                            .save(identificationDto).blockingGet()
                    return@map identificationDto.id
                }
    }

    fun getIdentificationId(): Single<String> {
        return fourthlineIdentificationRepository.getLastSavedLocalIdentification().map { it.id }
    }
}