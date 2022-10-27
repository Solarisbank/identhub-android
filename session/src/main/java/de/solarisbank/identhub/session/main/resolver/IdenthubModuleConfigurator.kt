package de.solarisbank.identhub.session.main.resolver

import de.solarisbank.identhub.session.feature.navigation.router.FIRST_STEP_DIRECTION
import de.solarisbank.identhub.session.main.resolver.config.FourthlineIdentificationConfig
import de.solarisbank.identhub.session.main.resolver.config.QesConfig
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
                    nextStep.contains(FIRST_STEP_DIRECTION.FOURTHLINE_SIGNING.destination)
                configModule = module {
                    single { FourthlineIdentificationConfig(isFourthlineSigning) }
                }
            }
            IdenthubModules.QESClassName -> {
                val isConfirmAcceptable =
                    nextStep.contains(FIRST_STEP_DIRECTION.FOURTHLINE_SIGNING.destination)
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