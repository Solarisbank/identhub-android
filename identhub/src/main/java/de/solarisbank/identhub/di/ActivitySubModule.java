package de.solarisbank.identhub.di;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences;
import de.solarisbank.sdk.feature.di.internal.Factory2;
import de.solarisbank.sdk.feature.di.internal.Provider;
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory;

public final class ActivitySubModule {

    public IdentificationStepPreferences provideIdentificationStepPreferences(SharedPreferences sharedPreferences) {
        return new IdentificationStepPreferences(sharedPreferences);
    }

    @NonNull
    public AssistedViewModelFactory provideViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> classProviderMap,
                                                            Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> classSavedStateProviderMap) {
        return new AssistedViewModelFactory(classSavedStateProviderMap, classProviderMap);
    }
}
