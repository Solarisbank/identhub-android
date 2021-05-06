package de.solarisbank.sdk.core.di

import android.content.Context
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions

class LibraryModuleContextFactory(private val libraryModule: LibraryModule) : Factory<Context> {
    override fun get(): Context {
        return Preconditions.checkNotNull(
                libraryModule.provideApplicationContext(),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(libraryModule: LibraryModule): LibraryModuleContextFactory {
            return LibraryModuleContextFactory(libraryModule)
        }
    }
}