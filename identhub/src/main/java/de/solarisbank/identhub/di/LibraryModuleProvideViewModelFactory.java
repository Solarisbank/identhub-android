package de.solarisbank.identhub.di;

import androidx.lifecycle.ViewModel;

import java.util.Map;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Preconditions;
import de.solarisbank.identhub.di.internal.Provider;

public class LibraryModuleProvideViewModelFactory implements Factory<ViewModelFactory> {

    private final LibraryModule libraryModule;

    private final Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> mapOfClassOfAndProviderOfViewModelProvider;

    public LibraryModuleProvideViewModelFactory(
            LibraryModule libraryModule,
            Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> mapOfClassOfAndProviderOfViewModelProvider) {
        this.libraryModule = libraryModule;
        this.mapOfClassOfAndProviderOfViewModelProvider = mapOfClassOfAndProviderOfViewModelProvider;
    }

    public static LibraryModuleProvideViewModelFactory create(
            LibraryModule libraryModule,
            Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> mapOfClassOfAndProviderOfViewModelProvider
    ) {
        return new LibraryModuleProvideViewModelFactory(libraryModule, mapOfClassOfAndProviderOfViewModelProvider);
    }

    @Override
    public ViewModelFactory get() {
        return Preconditions.checkNotNull(
                libraryModule.provideViewModelFactory(mapOfClassOfAndProviderOfViewModelProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
