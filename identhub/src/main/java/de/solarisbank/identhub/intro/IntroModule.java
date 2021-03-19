package de.solarisbank.identhub.intro;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;

import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.domain.session.SessionUrlRepository;

public final class IntroModule {

    @NonNull
    public IntroActivityViewModel provideViewModel(SavedStateHandle savedStateHandle, IdentificationStepPreferences identificationStepPreferences, SessionUrlRepository sessionUrlRepository) {
        return new IntroActivityViewModel(savedStateHandle, identificationStepPreferences, sessionUrlRepository);
    }
}
