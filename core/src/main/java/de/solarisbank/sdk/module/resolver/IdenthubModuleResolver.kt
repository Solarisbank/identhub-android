package de.solarisbank.sdk.module.resolver

import de.solarisbank.sdk.module.abstraction.IdenthubModule
import de.solarisbank.sdk.module.abstraction.IdenthubModuleFactory
import de.solarisbank.sdk.module.di.SharedModuleServiceLocator
import de.solarisbank.sdk.module.flows.qes.QESCoordinatorFactory
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

class IdenthubModuleResolver: IdenthubModuleFactory {
    override fun makeQES(): IdenthubModule? {
        return makeModule(QESClassName)
    }

    @Suppress("UNCHECKED_CAST")
    fun <Module> makeModule(className: String): Module? {
        return try {
            val kClass = Class.forName(className).kotlin
            kClass.createInstance() as? Module
        } catch (throwable: Throwable) {
            null
        }
    }

    companion object {
        const val QESClassName = "de.solarisbank.identhub.qes.QESModule"
    }
}