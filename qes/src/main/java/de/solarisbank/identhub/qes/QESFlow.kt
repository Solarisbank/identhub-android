package de.solarisbank.identhub.qes

import androidx.annotation.Keep
import de.solarisbank.identhub.qes.contract.ContractViewModel
import de.solarisbank.identhub.qes.di.core.QesKoin
import de.solarisbank.sdk.module.abstraction.IdenthubFlow

@Suppress("unused")
class QESFlow @Keep constructor(): IdenthubFlow {
    companion object {
        val navigationId = R.id.identhub_qes_navigation
        val viewModelClass = ContractViewModel::class.java
    }

    override val navigationResId: Int = R.navigation.identhub_nav_qes

    override fun load() {
        QesKoin.loadModules()
    }

    override fun unload() {
        QesKoin.unloadModules()
    }
}