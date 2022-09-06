package de.solarisbank.sdk.module.abstraction

interface FlowCoordinator<Input, Output: FlowCoordinatorOutput> {
    fun start(input: Input, callback: (Result<Output>) -> Unit)
}