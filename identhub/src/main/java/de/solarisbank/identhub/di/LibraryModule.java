package de.solarisbank.identhub.di;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import de.solarisbank.identhub.AssistedViewModelFactory;
import de.solarisbank.identhub.di.internal.Factory2;
import de.solarisbank.identhub.di.internal.Provider;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class LibraryModule {

    private final Application application;

    public LibraryModule(Application application) {
        this.application = application;
    }

    public Context provideApplicationContext() {
        return application;
    }

    @NonNull
    public AssistedViewModelFactory provideViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> classProviderMap,
                                                            Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> classSavedStateProviderMap) {
        return new AssistedViewModelFactory(classSavedStateProviderMap, classProviderMap);
    }
}
