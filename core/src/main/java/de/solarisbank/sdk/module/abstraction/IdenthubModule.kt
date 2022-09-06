package de.solarisbank.sdk.module.abstraction

interface IdenthubModule {
    val navigationResId: Int
    fun load()
    fun unload()
}