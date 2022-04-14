package de.solarisbank.identhub.example

import android.app.Application
import timber.log.Timber

@Suppress("unused")
class ExampleIdentHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}