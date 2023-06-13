package de.solarisbank.sdk.module.abstraction

/**
 * This interface can be used to specify classes that need to load/unload custom dependencies
 * based on the situation (outside of the original dependencies of the module that is for example
 * specified in BankKoin.kt)
 */
interface GeneralModuleLoader {
    /**
     * This method is called before the call to the load method on the module
      */
    fun loadBefore(moduleName: String, nextStep: String) {}
    /**
     * This method is called before the call to the load method on the module
     */
    fun loadAfter(moduleName: String, nextStep: String) {}
    fun unload(moduleName: String) {}
}