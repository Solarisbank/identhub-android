package de.solarisbank.sdk.module.abstraction

import de.solarisbank.sdk.module.di.SharedModuleServiceLocator
import de.solarisbank.sdk.module.flows.qes.QESCoordinatorFactory

interface IdenthubModuleFactory: ModuleFactory {
    fun makeQES(): IdenthubModule?
    fun makePhone(): IdenthubModule?
}