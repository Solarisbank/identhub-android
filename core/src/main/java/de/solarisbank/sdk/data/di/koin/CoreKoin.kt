package de.solarisbank.sdk.data.di.koin

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinApplication

object IdentHubKoinContext {
    private lateinit var app: KoinApplication

    fun setUpKoinApp(context: Context, sessionUrl: String?) {
        app = koinApplication {
            androidContext(context)
            modules(
                networkModule, SessionModule.get(sessionUrl)
            )
        }
    }

    internal fun getKoin(): Koin {
        return app.koin
    }
}

interface IdenthubKoinComponent: KoinComponent {
    override fun getKoin() = IdentHubKoinContext.getKoin()
}