package de.solarisbank.sdk.fourthline.domain.person

import android.annotation.SuppressLint
import de.solarisbank.sdk.data.IdentificationMethod
import de.solarisbank.identhub.session.module.config.FourthlineIdentificationConfig
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRepository
import io.reactivex.Single
import timber.log.Timber

class PersonDataUseCase(
    private val fourthlineIdentificationRepository: FourthlineIdentificationRepository,
    private val fourthlineIdentificationConfig: FourthlineIdentificationConfig,
    override val initialConfigStorage: InitialConfigStorage
) : SingleUseCase<Unit, PersonDataDto>(), NextStepSelector {

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

    private fun isIdentificationIdCreationRequired(identificationDto: IdentificationDto): Boolean {
        val method = identificationDto.method
        val nextStep = selectNextStep(identificationDto.nextStep, identificationDto.fallbackStep)
        val hasBankIdNextStep = nextStep?.contains(IdentificationMethod.BANK_ID.strValue) == true
        val hasBankNextStep = nextStep?.contains(IdentificationMethod.BANK.strValue) == true
        return if (method == IdentificationMethod.BANK_ID.strValue && hasBankIdNextStep) {
            return false
        } else if (method == IdentificationMethod.BANK.strValue && !hasBankIdNextStep && hasBankNextStep) {
            return false
        } else {
            true
        }
    }
}