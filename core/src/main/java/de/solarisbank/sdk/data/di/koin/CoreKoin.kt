package de.solarisbank.sdk.data.di.koin

import android.content.Context
import de.solarisbank.sdk.core.BuildConfig
import de.solarisbank.sdk.data.StartIdenthubConfig
import de.solarisbank.sdk.logger.IdLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.reflect.full.createInstance

object IdentHubKoinContext {
    private var app: KoinApplication? = null
    internal var mockro: MockroInterface? = null

    fun setUpKoinApp(context: Context, config: StartIdenthubConfig, mockroEnabled: Boolean = false) {
        if (app != null) return

        val startConfigModule = module { single { config } }
        app = koinApplication {
            androidContext(context)
            modules(
                startConfigModule, networkModule, SessionModule.get(config.sessionUrl),
                customizationModule, loggerModule, initialConfigModule, sharedUtilsModule
            )
        }
        if (mockroEnabled)
            setUpMockro()

        // Inject IdLogger with the now available LoggerUseCase
        IdLogger.inject(getKoin().get(), getKoin().get())
    }

    private fun setUpMockro() {
        if (BuildConfig.DEBUG) {
            try {
                val kClass = Class.forName("de.solarisbank.identhub.mockro.Mockro").kotlin
                mockro = kClass.createInstance() as? MockroInterface
                mockro?.loadModules(getKoin())
                mockro?.setPersona(MockroPersona.BankHappyPath)
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
    }

    fun unloadModules(moduleList: List<Module>) {
        getKoin().unloadModules(moduleList)
    }
}