package de.solarisbank.sdk.feature.di

import android.content.Context
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions

class ActivityModuleContextFactory(private val activityModule: ActivityModule) :
    Factory<Context> {
    override fun get(): Context {
        return Preconditions.checkNotNull(
                activityModule.provideContext(),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(activityModule: ActivityModule): ActivityModuleContextFactory {
            return ActivityModuleContextFactory(activityModule)
        }
    }
}