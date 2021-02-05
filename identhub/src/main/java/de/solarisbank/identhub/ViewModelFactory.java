package de.solarisbank.identhub;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import de.solarisbank.identhub.di.internal.Provider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModels;

    public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModels) {
        this.viewModels = viewModels;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Provider<? extends ViewModel> creator = viewModels.get(modelClass);
        if (creator == null) {
            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : viewModels.entrySet()) {
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                    break;
                }
            }
        }
        if (creator == null) {
            throw new IllegalArgumentException("unknown model class " + modelClass);
        }
        try {
            return (T) creator.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
