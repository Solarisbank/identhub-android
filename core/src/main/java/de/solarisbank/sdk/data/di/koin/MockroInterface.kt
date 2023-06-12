package de.solarisbank.sdk.data.di.koin

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