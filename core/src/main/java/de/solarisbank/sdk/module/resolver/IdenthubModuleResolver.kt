package de.solarisbank.sdk.module.resolver

import de.solarisbank.sdk.module.abstraction.IdenthubModule
import de.solarisbank.sdk.module.abstraction.IdenthubModuleFactory
import kotlin.reflect.full.createInstance

class IdenthubModuleResolver: IdenthubModuleFactory {
    override fun makeQES(): IdenthubModule? {
        return makeModule(QESClassName)
    }

    override fun makePhone(): IdenthubModule? {
        return makeModule(PhoneClassName)
    }

    @Suppress("UNCHECKED_CAST")
    fun makeModule(className: String): IdenthubModule? {
        return try {
            val kClass = Class.forName(className).kotlin
            kClass.createInstance() as? IdenthubModule
        } catch (throwable: Throwable) {
            null
        }
    }

    companion object {
        const val QESClassName = "de.solarisbank.identhub.qes.QESModule"
        const val PhoneClassName = "de.solarisbank.identhub.phone.PhoneModule"
        const val BankClassName = "de.solarisbank.identhub.bank.BankModule"
    }
}