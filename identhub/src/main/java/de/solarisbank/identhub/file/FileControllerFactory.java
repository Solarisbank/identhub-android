package de.solarisbank.identhub.file;

import android.content.Context;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Provider;

public class FileControllerFactory implements Factory<FileController> {
    private final Provider<Context> contextProvider;

    public FileControllerFactory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    public static FileControllerFactory create(Provider<Context> contextProvider) {
        return new FileControllerFactory(contextProvider);
    }

    @Override
    public FileController get() {
        return new FileController(contextProvider.get());
    }
}
