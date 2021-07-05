package de.solarisbank.identhub.intro;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.domain.session.SessionUrlRepository;
import de.solarisbank.identhub.router.COMPLETED_STEP;

import static de.solarisbank.identhub.session.IdentHub.SESSION_URL_KEY;

public final class IntroActivityViewModel extends ViewModel {

    private final IdentificationStepPreferences identificationStepPreferences;

    public IntroActivityViewModel(SavedStateHandle savedStateHandle,
                                  IdentificationStepPreferences identificationStepPreferences,
                                  SessionUrlRepository sessionUrlRepository) {
        if (!savedStateHandle.contains(SESSION_URL_KEY)) {
            throw new IllegalStateException("You have to initialize SDK with partner token");
        }

        sessionUrlRepository.save(savedStateHandle.get(SESSION_URL_KEY));
        this.identificationStepPreferences = identificationStepPreferences;
    }

    public COMPLETED_STEP getLastCompletedStep() {
        return identificationStepPreferences.get();
    }
}
