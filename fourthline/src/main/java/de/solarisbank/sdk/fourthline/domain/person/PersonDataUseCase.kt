package de.solarisbank.sdk.fourthline.domain.person

import android.annotation.SuppressLint
import de.solarisbank.identhub.session.feature.navigation.router.isIdentificationIdCreationRequired
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import de.solarisbank.identhub.session.main.resolver.config.FourthlineIdentificationConfig
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRepository
import io.reactivex.Single
import timber.log.Timber

class PersonDataUseCase(
        private val fourthlineIdentificationRepository: FourthlineIdentificationRepository,
        private val fourthlineIdentificationConfig: FourthlineIdentificationConfig
) : SingleUseCase<Unit, PersonDataDto>() {

    /**
     * Obtains identificationId, saves it and obtain personal data
     */
    override fun invoke(param: Unit): Single<NavigationalResult<PersonDataDto>> {

        return fourthlineIdentificationRepository.getLastSavedLocalIdentification()
            .map { entity ->
                if (!fourthlineIdentificationConfig.isFourthlineSigning) {
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
            .map { NavigationalResult(it) }
    }

    @SuppressLint("CheckResult")
    private fun passFourthlineIdentificationCreation(): Single<String> {
        Timber.d("passFourthlineIdentificationCreation()")
        return if (!fourthlineIdentificationConfig.isFourthlineSigning) {
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
}