package de.solarisbank.identhub.example

import android.app.Application
import timber.log.Timber

class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
//        }
    }
}