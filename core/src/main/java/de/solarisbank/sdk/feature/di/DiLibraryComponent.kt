package de.solarisbank.sdk.feature.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import de.solarisbank.sdk.feature.base.BaseActivity
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Provider

class DiLibraryComponent private constructor(libraryModule: LibraryModule) : LibraryComponent {

    private val applicationContextProvider: Provider<Context> = DoubleCheck.provider<LibraryModuleContextFactory, Context>(
        LibraryModuleContextFactory.create(libraryModule)
    )

    override fun activityComponent(): CoreActivityComponent.Factory = CoreActivityComponentFactory()

    override fun applicationContext(): Context {
        return applicationContextProvider.get()
    }

    private class Builder {
        lateinit var libraryModule: LibraryModule

        fun setLibraryModule(libraryModule: LibraryModule) = apply { this.libraryModule = libraryModule }

        fun build() = DiLibraryComponent(libraryModule)
    }

    private class CoreActivityComponentFactory : CoreActivityComponent.Factory {
        override fun create(baseActivity: BaseActivity): CoreActivityComponent {
            return CoreActivityComponentImpl(ActivityModule(baseActivity))
        }
    }

    private class CoreActivityComponentImpl(activityModule: ActivityModule) :
        CoreActivityComponent {

        private val contextProvider: Provider<Context> = DoubleCheck.provider(
            ActivityModuleContextFactory.create(activityModule)
        )

        val sharedPreferencesProvider: Provider<SharedPreferences> = DoubleCheck.provider(
            SharedPreferencesFactory.create(activityModule, contextProvider)
        )
        override fun context(): Context {
            return contextProvider.get()
        }

        override fun sharedPreferences(): SharedPreferences {
            return sharedPreferencesProvider.get()
        }


        override fun inject(introActivity: BaseActivity) {

        }
    }

    companion object {
        private var libraryComponent: LibraryComponent? = null
        private val lock = Unit

        fun getInstance(application: Application): LibraryComponent {
            synchronized(lock) {

                return libraryComponent
                        ?: return Builder()
                                .setLibraryModule(LibraryModule(application))
                                .build()
                                .apply { libraryComponent = this }
            }
        }
    }
}