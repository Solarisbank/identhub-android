package de.solarisbank.sdk.fourthline.domain.identification

import de.solarisbank.identhub.session.module.config.FourthlineIdentificationConfig
import de.solarisbank.sdk.data.IdentificationMethod
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationDataSource
import de.solarisbank.sdk.fourthline.data.person.PersonDataSource

class FourthlineIdentificationUseCase(
    private val fourthlineIdentificationDataSource: FourthlineIdentificationDataSource,
    private val personDataSource: PersonDataSource,
    private val identificationLocalDataSource: IdentificationLocalDataSource,
    private val fourthlineIdentificationConfig: FourthlineIdentificationConfig,
    override val initialConfigStorage: InitialConfigStorage
) :  NextStepSelector {

    /**
     * Obtains identificationId, saves it and obtain personal data
     */

    suspend fun createIdentification(): IdentificationDto {
        val storedIdentification = identificationLocalDataSource.getIdentification()
        return if (storedIdentification == null || isIdentificationIdCreationRequired(storedIdentification)) {
            if (fourthlineIdentificationConfig.isFourthlineSigning) {
                fourthlineIdentificationDataSource.createFourthlineSigningIdentification()
            } else {
                fourthlineIdentificationDataSource.createFourthlineIdentification()
            }.also {
                identificationLocalDataSource.saveIdentification(it)
            }
        } else {
            storedIdentification
        }
    }

    suspend fun getPersonData(identificationId: String): PersonDataDto {
        return personDataSource.getPersonData(identificationId)
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