package de.solarisbank.identhub.startup

import androidx.annotation.Keep
import de.solarisbank.identhub.startup.di.PhoneKoin
import de.solarisbank.sdk.module.abstraction.IdenthubFlow

class PhoneFlow @Keep constructor(): IdenthubFlow {
    companion object {
        val navigationId = R.id.identhub_phone_navigation
    }

    override val navigationResId: Int = R.navigation.identhub_nav_phone

    override fun load() {
        PhoneKoin.loadModules()
    }

    override fun unload() {
        PhoneKoin.unloadModules()
    }
}