package de.solarisbank.identhub.session.main.resolver

import de.solarisbank.identhub.session.feature.navigation.router.FIRST_STEP_DIRECTION
import de.solarisbank.identhub.session.feature.navigation.router.NEXT_STEP_DIRECTION
import de.solarisbank.identhub.session.main.resolver.IdenthubModules.BankClassName
import de.solarisbank.identhub.session.main.resolver.IdenthubModules.FourthlineClassName
import de.solarisbank.identhub.session.main.resolver.IdenthubModules.PhoneClassName
import de.solarisbank.identhub.session.main.resolver.IdenthubModules.QESClassName
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
        return when(nextStep) {
            NEXT_STEP_DIRECTION.BANK_ID_IBAN.destination,
            NEXT_STEP_DIRECTION.BANK_IBAN.destination ->
                ResolvedModule.Module(BankClassName, nextStep)
            NEXT_STEP_DIRECTION.BANK_QES.destination,
            FIRST_STEP_DIRECTION.QES.destination,
            NEXT_STEP_DIRECTION.BANK_ID_QES.destination,
            NEXT_STEP_DIRECTION.FOURTHLINE_SIGNING_QES.destination->
                ResolvedModule.Module(QESClassName, nextStep)
            NEXT_STEP_DIRECTION.FOURTHLINE_SIMPLIFIED.destination,
            NEXT_STEP_DIRECTION.BANK_ID_FOURTHLINE.destination,
            NEXT_STEP_DIRECTION.FOURTHLINE_SIGNING.destination ->
                ResolvedModule.Module(FourthlineClassName, nextStep)
            NEXT_STEP_DIRECTION.MOBILE_NUMBER.destination ->
                ResolvedModule.Module(PhoneClassName, nextStep)
            NEXT_STEP_DIRECTION.ABORT.destination -> ResolvedModule.Abort
            else -> ResolvedModule.UnknownModule
        }
    }
}

sealed class ResolvedModule {
    data class Module(val className: String, val nextStep: String): ResolvedModule()
    object Abort: ResolvedModule()
    object UnknownModule: ResolvedModule()
}