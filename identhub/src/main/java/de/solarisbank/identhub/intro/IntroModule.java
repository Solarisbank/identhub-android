package de.solarisbank.identhub.intro;

import androidx.annotation.NonNull;

public class IntroModule {

    @NonNull
    public IntroActivityViewModel provideViewModel() {
        return new IntroActivityViewModel();
    }
}
