package de.solarisbank.identhub.verfication.phone;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class VerificationPhoneFragmentInjector implements MembersInjector<VerificationPhoneFragment> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public VerificationPhoneFragmentInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(VerificationPhoneFragment instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(VerificationPhoneFragment instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
