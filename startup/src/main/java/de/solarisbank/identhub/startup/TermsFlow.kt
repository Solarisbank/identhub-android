package de.solarisbank.identhub.startup

import de.solarisbank.identhub.startup.di.PhoneKoin
import de.solarisbank.sdk.module.abstraction.IdenthubFlow

class TermsFlow : IdenthubFlow {
    companion object {
        val navigationId = R.id.identhub_nav_terms
    }

    override val navigationResId: Int = R.navigation.identhub_nav_terms

    override fun load() {
        PhoneKoin.loadModules()
    }

    override fun unload() {
        PhoneKoin.unloadModules()
    }
}