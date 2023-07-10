package de.solarisbank.identhub.startup.di

import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import org.koin.core.module.Module

object TermsKoin: IdenthubKoinComponent {
    private val moduleList = listOf<Module>()

    fun loadModules() {
        loadModules(moduleList)
    }

    fun unloadModules() {
        unloadModules(moduleList)
    }
}