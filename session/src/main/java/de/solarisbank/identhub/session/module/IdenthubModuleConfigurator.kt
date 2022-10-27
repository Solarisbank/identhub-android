package de.solarisbank.identhub.session.module

import de.solarisbank.sdk.data.IdentificationStep
import de.solarisbank.identhub.session.module.config.FourthlineIdentificationConfig
import de.solarisbank.identhub.session.module.config.QesConfig
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import org.koin.core.module.Module
import org.koin.dsl.module

class IdenthubModuleConfigurator: IdenthubKoinComponent {
    fun configure(module: ResolvedModule.Module) {
        val nextStep = module.nextStep
        var configModule: Module? = null
        when (module.className) {
            IdenthubModules.FourthlineClassName -> {
                val isFourthlineSigning =
                    nextStep.contains(IdentificationStep.FOURTHLINE_SIGNING.destination)
                configModule = module {
                    single { FourthlineIdentificationConfig(isFourthlineSigning) }
                }
            }
            IdenthubModules.QESClassName -> {
                val isConfirmAcceptable =
                    nextStep.contains(IdentificationStep.FOURTHLINE_SIGNING.destination)
                configModule = module {
                    single { QesConfig(isConfirmAcceptable) }
                }
            }
        }
        configModule?.let {
            loadModules(listOf(it))
        }
    }
}