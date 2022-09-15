package de.solarisbank.identhub.phone

import de.solarisbank.identhub.phone.di.PhoneKoin
import de.solarisbank.sdk.module.abstraction.IdenthubModule

class PhoneModule : IdenthubModule {
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