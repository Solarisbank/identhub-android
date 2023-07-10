package de.solarisbank.identhub.session.main

import android.os.Bundle
import de.solarisbank.identhub.session.module.IdenthubModuleResolver
import de.solarisbank.identhub.session.module.ModuleOutcome
import de.solarisbank.identhub.session.module.ResolvedModule
import de.solarisbank.identhub.session.module.outcome.TermsModuleOutcome
import de.solarisbank.identhub.session.module.outcome.PhoneModuleOutcome
import de.solarisbank.sdk.data.IdenthubResult
import de.solarisbank.sdk.data.IdentificationStep
import de.solarisbank.sdk.data.initial.InitialConfigStorage

class MainCoordinator(
    private val configStorage: InitialConfigStorage,
    private val moduleResolver: IdenthubModuleResolver,
    private val eventHandler: (MainCoordinatorEvent) -> Unit,
) : Navigator {

    private var phoneVerified = false
    private var termsAndConditionsAccepted = false
    private var firstStep: String? = null

    fun start(firstStep: String) {
        this.firstStep = firstStep
        processStartup()
    }

    private fun processStartup() {
        if (!termsAndConditionsAccepted)
            termsAndConditionsAccepted = configStorage.get().isTermsPreAccepted

        if (!phoneVerified)
            phoneVerified = configStorage.get().isPhoneNumberVerified

        if (!termsAndConditionsAccepted) {
            handleStep(IdentificationStep.TERMS_AND_CONDITIONS.destination)
        } else if (!phoneVerified) {
            handleStep(IdentificationStep.MOBILE_NUMBER.destination)
        } else {
            goToFirstStep()
        }
    }

    private fun goToFirstStep() = handleStep(firstStep ?: configStorage.get().firstStep)

    private fun handleStep(step: String) {
        handleNextStepOutcome(ModuleOutcome.NextStepOutcome(step))
    }

    override fun navigate(navigationId: Int, bundle: Bundle?) {
        eventHandler(MainCoordinatorEvent.Navigate(navigationId, bundle))
    }

    override fun onOutcome(outcome: ModuleOutcome?) {
        outcome ?: return

        when (outcome) {
            is ModuleOutcome.Finished -> handleFinishedOutcome(outcome)
            is ModuleOutcome.NextStepOutcome -> handleNextStepOutcome(outcome)
            is ModuleOutcome.Failure -> callFailure(outcome.errorMessage)
            is ModuleOutcome.Other -> handleOtherOutcome(outcome.outcome)
        }
    }

    private fun handleNextStepOutcome(outcome: ModuleOutcome.NextStepOutcome) {
        when (val resolved = moduleResolver.resolve(outcome.nextStep)) {
            is ResolvedModule.Module -> {
                eventHandler(MainCoordinatorEvent.ModuleChange(resolved))
            }
            is ResolvedModule.Abort -> callFailure("Identification Aborted")
            is ResolvedModule.UnknownModule -> callFailure("Couldn't find a module for step: ${outcome.nextStep}")
        }
    }

    private fun handleFinishedOutcome(outcome: ModuleOutcome.Finished) {
        val result = IdenthubResult.Confirmed(outcome.identificationId)
        eventHandler(MainCoordinatorEvent.ResultAvailable(result))
    }

    private fun handleOtherOutcome(outcome: Any) {
        when (outcome) {
            is PhoneModuleOutcome -> {
                phoneVerified = true
                processStartup()
            }
            is TermsModuleOutcome -> {
                termsAndConditionsAccepted = true
                processStartup()
            }
        }
    }

    private fun callFailure(message: String?) {
        eventHandler(MainCoordinatorEvent.ResultAvailable(IdenthubResult.Failed(message)))
    }
}

sealed class MainCoordinatorEvent {
    data class Navigate(val navigationId: Int, val bundle: Bundle?) : MainCoordinatorEvent()
    data class ModuleChange(val module: ResolvedModule.Module) : MainCoordinatorEvent()
    data class ResultAvailable(val result: IdenthubResult) : MainCoordinatorEvent()
}