package de.solarisbank.sdk.data.di.koin

import android.content.Context
import de.solarisbank.sdk.core.BuildConfig
import de.solarisbank.sdk.logger.IdLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import kotlin.reflect.full.createInstance

object IdentHubKoinContext {
    private var app: KoinApplication? = null
    internal var mockro: MockroInterface? = null

    fun setUpKoinApp(context: Context, sessionUrl: String, mockroEnabled: Boolean = false) {
        if (app != null) return
        app = koinApplication {
            androidContext(context)
            modules(
                networkModule, SessionModule.get(sessionUrl), customizationModule, loggerModule,
                initialConfigModule, sharedUtilsModule
            )
        }
        if (mockroEnabled)
            setUpMockro()
    }

    private fun setUpMockro() {
        if (BuildConfig.DEBUG) {
            try {
                val kClass = Class.forName("de.solarisbank.identhub.mockro.Mockro").kotlin
                mockro = kClass.createInstance() as? MockroInterface
                mockro?.loadModules(getKoin())
            } catch (throwable: Throwable) {
                IdLogger.debug("Mockro isn't available")
            }
        }
    }

    internal fun getKoin(): Koin {
        return app!!.koin
    }
}

interface IdenthubKoinComponent: KoinComponent {
    override fun getKoin() = IdentHubKoinContext.getKoin()

    fun loadModules(moduleList: List<Module>) {
        getKoin().loadModules(moduleList)
        IdentHubKoinContext.mockro?.loadModules(getKoin())
    }

    fun unloadModules(moduleList: List<Module>) {
        getKoin().unloadModules(moduleList)
    }
}