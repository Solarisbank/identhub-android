package de.solarisbank.identhub.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.Provider;

class LibraryModule {

    @NonNull
    public ViewModelFactory provideViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> classProviderMap) {
        return new ViewModelFactory(classProviderMap);
    }
}
