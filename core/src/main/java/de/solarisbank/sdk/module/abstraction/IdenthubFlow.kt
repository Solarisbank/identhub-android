package de.solarisbank.sdk.module.abstraction

interface IdenthubFlow {
    val navigationResId: Int
    fun load()
    fun unload()
}