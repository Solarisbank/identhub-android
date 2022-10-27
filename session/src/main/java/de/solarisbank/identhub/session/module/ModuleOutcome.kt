package de.solarisbank.identhub.session.module

sealed class ModuleOutcome {
    data class Finished(val identificationId: String, val finalStatus: String): ModuleOutcome()
    data class NextStepOutcome(val nextStep: String): ModuleOutcome()
    data class Failure(val errorMessage: String? = null): ModuleOutcome()
    data class Other(val outcome: Any): ModuleOutcome()
}