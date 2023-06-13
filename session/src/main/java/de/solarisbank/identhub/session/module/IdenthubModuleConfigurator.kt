package de.solarisbank.identhub.session.module

import de.solarisbank.identhub.session.module.config.BankConfig
import de.solarisbank.sdk.data.IdentificationStep
import de.solarisbank.identhub.session.module.config.FourthlineIdentificationConfig
import de.solarisbank.identhub.session.module.config.QesConfig
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.module.abstraction.GeneralModuleLoader
import org.koin.core.module.Module
import org.koin.dsl.module

class IdenthubModuleConfigurator: GeneralModuleLoader, IdenthubKoinComponent {
    override fun loadBefore(moduleName: String, nextStep: String) {
        var configModule: Module? = null
        when (moduleName) {
            IdenthubModules.FourthlineClassName -> {
                val isFourthlineSigning =
                    nextStep.contains(IdentificationStep.FOURTHLINE_SIGNING.destination)
                val shouldShowNamirialTerms = shouldShowNamirialTerms(nextStep)
                configModule = module {
                    single {
                        FourthlineIdentificationConfig(
                            isFourthlineSigning,
                            shouldShowNamirialTerms
                        )
                    }
                }
            }
            IdenthubModules.QESClassName -> {
                val isConfirmAcceptable =
                    nextStep.contains(IdentificationStep.FOURTHLINE_SIGNING.destination)
                configModule = module {
                    single { QesConfig(isConfirmAcceptable) }
                }
            }
            IdenthubModules.BankClassName -> {
                val isBankId = nextStep == IdentificationStep.BANK_ID_IBAN.destination
                configModule = module {
                    single { BankConfig(isBankId) }
                }
            }
        }
        configModule?.let {
            loadModules(listOf(it))
        }
    }
    fun configure(module: ResolvedModule.Module) {

    }
}

private fun shouldShowNamirialTerms(method: String): Boolean {
    return when (method) {
        IdentificationStep.FOURTHLINE_SIGNING.destination,
        IdentificationStep.BANK_ID_FOURTHLINE.destination -> true
        else -> false
    }
}