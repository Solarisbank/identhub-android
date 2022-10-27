package de.solarisbank.identhub.session.module

import de.solarisbank.sdk.data.IdentificationStep
import de.solarisbank.sdk.data.IdentificationStep.*
import de.solarisbank.identhub.session.module.IdenthubModules.BankClassName
import de.solarisbank.identhub.session.module.IdenthubModules.FourthlineClassName
import de.solarisbank.identhub.session.module.IdenthubModules.PhoneClassName
import de.solarisbank.identhub.session.module.IdenthubModules.QESClassName
import de.solarisbank.sdk.module.abstraction.IdenthubModule
import kotlin.reflect.full.createInstance

class IdenthubModuleResolver {
    @Suppress("UNCHECKED_CAST")
    fun makeModule(className: String): IdenthubModule? {
        return try {
            val kClass = Class.forName(className).kotlin
            kClass.createInstance() as? IdenthubModule
        } catch (throwable: Throwable) {
            null
        }
    }

    fun resolve(nextStep: String): ResolvedModule {
        return when(IdentificationStep.from(nextStep)) {
            BANK_ID_IBAN,
            BANK_IBAN ->
                ResolvedModule.Module(BankClassName, nextStep)
            BANK_QES,
            QES,
            BANK_ID_QES,
            FOURTHLINE_SIGNING_QES->
                ResolvedModule.Module(QESClassName, nextStep)
            FOURTHLINE_SIMPLIFIED,
            BANK_ID_FOURTHLINE,
            FOURTHLINE_SIGNING ->
                ResolvedModule.Module(FourthlineClassName, nextStep)
            MOBILE_NUMBER ->
                ResolvedModule.Module(PhoneClassName, nextStep)
            ABORT -> ResolvedModule.Abort
            null -> ResolvedModule.UnknownModule
        }
    }
}

sealed class ResolvedModule {
    data class Module(val className: String, val nextStep: String): ResolvedModule()
    object Abort: ResolvedModule()
    object UnknownModule: ResolvedModule()
}