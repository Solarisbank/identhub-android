package de.solarisbank.identhub.intro;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.di.internal.Factory2;
import de.solarisbank.identhub.di.internal.Provider;
import de.solarisbank.identhub.domain.session.SessionUrlRepository;

public final class IntroActivityViewModelFactory implements Factory2<ViewModel, SavedStateHandle> {
    private final IntroModule introModule;
    private final Provider<IdentificationStepPreferences> identificationStepPreferencesProvider;
    private final Provider<SessionUrlRepository> sessionUrlRepositoryProvider;

    public IntroActivityViewModelFactory(IntroModule introModule,
                                         Provider<IdentificationStepPreferences> identificationStepPreferencesProvider,
                                         Provider<SessionUrlRepository> sessionUrlRepositoryProvider) {
        this.introModule = introModule;
        this.identificationStepPreferencesProvider = identificationStepPreferencesProvider;
        this.sessionUrlRepositoryProvider = sessionUrlRepositoryProvider;
    }

    public static IntroActivityViewModelFactory create(IntroModule introModule,
                                                       Provider<IdentificationStepPreferences> identificationStepPreferencesProvider,
                                                       Provider<SessionUrlRepository> sessionUrlRepositoryProvider) {
        return new IntroActivityViewModelFactory(introModule, identificationStepPreferencesProvider, sessionUrlRepositoryProvider);
    }

    @Override
    public ViewModel create(SavedStateHandle value) {
        return introModule.provideViewModel(value, identificationStepPreferencesProvider.get(), sessionUrlRepositoryProvider.get());
    }
}
