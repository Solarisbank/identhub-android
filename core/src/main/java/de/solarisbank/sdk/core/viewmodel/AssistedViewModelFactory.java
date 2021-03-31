package de.solarisbank.sdk.core.viewmodel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.savedstate.SavedStateRegistryOwner;

import java.util.Map;

import de.solarisbank.sdk.core.di.internal.Factory2;
import de.solarisbank.sdk.core.di.internal.Provider;

public final class AssistedViewModelFactory {

    private final Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> saveStateViewModels;
    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModels;

    public AssistedViewModelFactory(Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> saveStateViewModels,
                                    Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModels) {
        this.saveStateViewModels = saveStateViewModels;
        this.viewModels = viewModels;
    }

    public ViewModelProvider.Factory create(@NonNull SavedStateRegistryOwner owner, @Nullable Bundle defaultArgs) {
        return new AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            @NonNull
            @Override
            protected <T extends ViewModel> T create(@NonNull String key, @NonNull Class<T> modelClass, @NonNull SavedStateHandle handle) {
                ViewModel viewModel = createAssistedViewModel(key, modelClass, handle);
                if (viewModel == null) {
                    viewModel = createViewModel(modelClass);

                    if (viewModel == null) {
                        throw new IllegalArgumentException("unknown model class " + modelClass);
                    }
                }

                return (T) viewModel;
            }
        };
    }

    @Nullable
    private <T extends ViewModel> T createAssistedViewModel(@NonNull String key, @NonNull Class<T> modelClass, @NonNull SavedStateHandle handle) {
        Factory2<? extends ViewModel, SavedStateHandle> creator = saveStateViewModels.get(modelClass);
        if (creator == null) {
            for (Map.Entry<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> entry : saveStateViewModels.entrySet()) {
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                    break;
                }
            }
        }
        if (creator != null) {
            return (T) creator.create(handle);
        }
        return null;
    }

    @Nullable
    private <T extends ViewModel> T createViewModel(@NonNull Class<T> modelClass) {
        Provider<? extends ViewModel> creator = viewModels.get(modelClass);
        if (creator == null) {
            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : viewModels.entrySet()) {
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                    break;
                }
            }
        }

        if (creator != null) {
            return (T) creator.get();
        }
        return null;
    }
}
