package de.solarisbank.identhub.intro;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;

public class IntroActivityViewModelFactory implements Factory<ViewModel> {
    private final IntroModule introModule;

    public IntroActivityViewModelFactory(IntroModule introModule) {
        this.introModule = introModule;
    }

    public static IntroActivityViewModelFactory create(IntroModule introModule) {
        return new IntroActivityViewModelFactory(introModule);
    }

    @Override
    public ViewModel get() {
        return introModule.provideViewModel();
    }
}
