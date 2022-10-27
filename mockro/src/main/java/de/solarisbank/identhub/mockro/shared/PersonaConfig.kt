package de.solarisbank.identhub.mockro.shared

import de.solarisbank.identhub.mockro.Mockro
import de.solarisbank.sdk.data.IdentificationStep
import de.solarisbank.sdk.data.di.koin.MockroPersona

data class PersonaConfig(
    val firstStep: String,
    val fourthlineStep: String?,
    val qesStep: String?,
) {
    companion object {
        val current: PersonaConfig =
            when (Mockro.currentPersona) {
                is MockroPersona.FourthlineSigningHappyPath -> PersonaConfig(
                    firstStep = IdentificationStep.FOURTHLINE_SIGNING.destination,
                    fourthlineStep = IdentificationStep.FOURTHLINE_SIGNING.destination,
                    qesStep = IdentificationStep.FOURTHLINE_SIGNING_QES.destination
                )
                is MockroPersona.BankHappyPath -> PersonaConfig(
                    firstStep = IdentificationStep.BANK_IBAN.destination,
                    fourthlineStep = IdentificationStep.FOURTHLINE_SIMPLIFIED.destination,
                    qesStep = IdentificationStep.BANK_QES.destination
                )
                is MockroPersona.BankIdHappyPath -> PersonaConfig(
                    firstStep = IdentificationStep.BANK_ID_IBAN.destination,
                    fourthlineStep = IdentificationStep.BANK_ID_FOURTHLINE.destination,
                    qesStep = IdentificationStep.BANK_ID_QES.destination
                )
            }
    }
}