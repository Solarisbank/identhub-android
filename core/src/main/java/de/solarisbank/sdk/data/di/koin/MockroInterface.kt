package de.solarisbank.sdk.data.di.koin

import de.solarisbank.sdk.module.abstraction.GeneralModuleLoader
import org.koin.core.Koin

interface MockroInterface {
    fun loadModules(koin: Koin)
    fun setPersona(persona: MockroPersona, options: MockroOptions? = null)
}

sealed class MockroPersona {
    object BankHappyPath: MockroPersona()
    object FourthlineSigningHappyPath: MockroPersona()
    object BankIdHappyPath: MockroPersona()
    object FourthlineHappyPath: MockroPersona()
}

data class MockroOptions(
    val numberOfStartUpFailures: Int = 0
)

class MockroLoader: GeneralModuleLoader, IdenthubKoinComponent {
    override fun loadAfter(moduleName: String, nextStep: String) {
        IdentHubKoinContext.mockro?.loadModules(getKoin())
    }
}