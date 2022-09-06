package de.solarisbank.sdk.module.flows.qes

import de.solarisbank.sdk.module.abstraction.ModuleFactory

interface QESCoordinatorFactory: ModuleFactory {
    fun makeQESCoordinator(): QESCoordinator
}