package de.solarisbank.identhub.di;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import de.solarisbank.identhub.AssistedViewModelFactory;
import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Factory2;
import de.solarisbank.identhub.di.internal.Preconditions;
import de.solarisbank.identhub.di.internal.Provider;

public final class LibraryModuleAssistedViewModelFactory implements Factory<AssistedViewModelFactory> {

    private final LibraryModule libraryModule;

    private final Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> mapOfClassOfAndProviderOfViewModelProvider;
    private final Provider<Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>>> mapOfClassOfAndProviderOfSavedViewModelProvider;

    public LibraryModuleAssistedViewModelFactory(
            LibraryModule libraryModule,
            Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> mapOfClassOfAndProviderOfViewModelProvider,
            Provider<Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>>> mapOfClassOfAndProviderOfSavedViewModelProvider) {
        this.libraryModule = libraryModule;
        this.mapOfClassOfAndProviderOfViewModelProvider = mapOfClassOfAndProviderOfViewModelProvider;
        this.mapOfClassOfAndProviderOfSavedViewModelProvider = mapOfClassOfAndProviderOfSavedViewModelProvider;
    }

    public static LibraryModuleAssistedViewModelFactory create(
            LibraryModule libraryModule,
            Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> mapOfClassOfAndProviderOfViewModelProvider,
            Provider<Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>>> mapOfClassOfAndProviderOfSavedViewModelProvider
    ) {
        return new LibraryModuleAssistedViewModelFactory(libraryModule, mapOfClassOfAndProviderOfViewModelProvider, mapOfClassOfAndProviderOfSavedViewModelProvider);
    }

    @Override
    public AssistedViewModelFactory get() {
        return Preconditions.checkNotNull(
                libraryModule.provideViewModelFactory(mapOfClassOfAndProviderOfViewModelProvider.get(), mapOfClassOfAndProviderOfSavedViewModelProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
