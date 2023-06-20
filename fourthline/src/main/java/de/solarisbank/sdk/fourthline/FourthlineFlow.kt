package de.solarisbank.sdk.fourthline

import androidx.annotation.Keep
import de.solarisbank.sdk.fourthline.di.FourthlineKoin
import de.solarisbank.sdk.module.abstraction.IdenthubFlow

class FourthlineFlow @Keep constructor(): IdenthubFlow {
    companion object {
        val navigationId = R.id.identhub_fourthline_navigation
    }

    override val navigationResId: Int = R.navigation.identhub_fourthline_nav_graph

    override fun load() {
        FourthlineKoin.loadModules()
    }

    override fun unload() {
        FourthlineKoin.unloadModules()
    }
}