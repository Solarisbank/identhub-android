package de.solarisbank.identhub.mockro.shared

import de.solarisbank.identhub.mockro.Mockro
import de.solarisbank.identhub.session.feature.navigation.router.NEXT_STEP_DIRECTION
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
                    firstStep = NEXT_STEP_DIRECTION.FOURTHLINE_SIGNING.destination,
                    fourthlineStep = NEXT_STEP_DIRECTION.FOURTHLINE_SIGNING.destination,
                    qesStep = NEXT_STEP_DIRECTION.FOURTHLINE_SIGNING_QES.destination
                )
                is MockroPersona.BankHappyPath -> PersonaConfig(
                    firstStep = NEXT_STEP_DIRECTION.BANK_IBAN.destination,
                    fourthlineStep = NEXT_STEP_DIRECTION.FOURTHLINE_SIMPLIFIED.destination,
                    qesStep = NEXT_STEP_DIRECTION.BANK_QES.destination
                )
                is MockroPersona.BankIdHappyPath -> PersonaConfig(
                    firstStep = NEXT_STEP_DIRECTION.BANK_ID_IBAN.destination,
                    fourthlineStep = NEXT_STEP_DIRECTION.BANK_ID_FOURTHLINE.destination,
                    qesStep = NEXT_STEP_DIRECTION.BANK_ID_QES.destination
                )
            }
    }
}