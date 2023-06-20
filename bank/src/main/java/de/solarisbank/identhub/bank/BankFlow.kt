package de.solarisbank.identhub.bank

import androidx.annotation.Keep
import de.solarisbank.identhub.bank.di.BankKoin
import de.solarisbank.sdk.module.abstraction.IdenthubFlow

class BankFlow @Keep constructor(): IdenthubFlow {
    companion object {
        val navigationId = R.id.identhub_bank_navigation
    }

    override val navigationResId: Int = R.navigation.identhub_bank_nav_graph

    override fun load() {
        BankKoin.loadModules()
    }

    override fun unload() {
        BankKoin.unloadModules()
    }
}