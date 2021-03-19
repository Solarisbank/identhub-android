package de.solarisbank.identhub.di;

import android.content.Context;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Preconditions;

public final class LibraryModuleContextFactory implements Factory<Context> {

    private final LibraryModule libraryModule;

    public LibraryModuleContextFactory(LibraryModule libraryModule) {
        this.libraryModule = libraryModule;
    }

    public static LibraryModuleContextFactory create(LibraryModule libraryModule) {
        return new LibraryModuleContextFactory(libraryModule);
    }

    @Override
    public Context get() {
        return Preconditions.checkNotNull(
                libraryModule.provideApplicationContext(),
                "Cannot return null from provider method"
        );
    }
}
