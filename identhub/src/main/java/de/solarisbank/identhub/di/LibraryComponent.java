package de.solarisbank.identhub.di;


import androidx.lifecycle.ViewModel;

import java.util.Map;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.DoubleCheck;
import de.solarisbank.identhub.di.internal.Provider;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewFragment;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewFragmentInjector;
import de.solarisbank.identhub.contract.sign.ContractSigningFragment;
import de.solarisbank.identhub.contract.sign.ContractSigningFragmentInjector;
import de.solarisbank.identhub.identity.IdentityActivity;
import de.solarisbank.identhub.identity.IdentityActivityInjector;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.identity.summary.IdentitySummaryActivity;
import de.solarisbank.identhub.identity.summary.IdentitySummaryFragment;
import de.solarisbank.identhub.intro.IntroActivity;
import de.solarisbank.identhub.intro.IntroActivityInjector;
import de.solarisbank.identhub.intro.IntroModule;
import de.solarisbank.identhub.progress.ProgressIndicatorFragment;
import de.solarisbank.identhub.progress.ProgressIndicatorFragmentInjector;
import de.solarisbank.identhub.verfication.bank.VerificationBankFragment;
import de.solarisbank.identhub.verfication.bank.VerificationBankFragmentInjector;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorMessageFragment;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorMessageFragmentInjector;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragment;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragmentInjector;
import de.solarisbank.identhub.verfication.bank.success.VerificationBankSuccessMessageFragment;
import de.solarisbank.identhub.verfication.bank.success.VerificationBankSuccessMessageFragmentInjector;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneFragment;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneFragmentInjector;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorMessageFragment;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorMessageFragmentInjector;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragment;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragmentInjector;

public class LibraryComponent {

    private static final Object lock = new Object();
    private static LibraryComponent libraryComponent = null;

    private final IdentityModule identityModule;
    private final IntroModule introModule;
    private final LibraryModule libraryModule;

    private Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> mapOfClassOfAndProviderOfViewModelProvider;

    private Provider<ViewModelFactory> viewModelProvider;

    private LibraryComponent(
            IdentityModule identityModule,
            IntroModule introModule,
            LibraryModule libraryModule) {
        this.identityModule = identityModule;
        this.introModule = introModule;
        this.libraryModule = libraryModule;
        initialize();
    }

    public static LibraryComponent getInstance() {
        synchronized (lock) {
            if (libraryComponent == null) {
                libraryComponent = new LibraryComponent.Builder()
                        .setIdentityModule(new IdentityModule())
                        .setIntroModule(new IntroModule())
                        .setLibraryModule(new LibraryModule())
                        .build();
            }
            return libraryComponent;
        }
    }

    private void initialize() {
        mapOfClassOfAndProviderOfViewModelProvider = ViewModelMapProvider.create(identityModule, introModule);
        viewModelProvider = DoubleCheck.provider(LibraryModuleProvideViewModelFactory.create(libraryModule, mapOfClassOfAndProviderOfViewModelProvider));
    }

    public void inject(IntroActivity introActivity) {
        IntroActivityInjector.injectViewModelFactory(introActivity, viewModelProvider.get());
    }

    public void inject(IdentityActivity identityActivity) {
        IdentityActivityInjector.injectViewModelFactory(identityActivity, viewModelProvider.get());
    }

    public void inject(VerificationPhoneFragment verificationPhoneFragment) {
        VerificationPhoneFragmentInjector.injectViewModelFactory(verificationPhoneFragment, viewModelProvider.get());
    }

    public void inject(VerificationPhoneSuccessMessageFragment successMessageFragment) {
        VerificationPhoneSuccessMessageFragmentInjector.injectViewModelFactory(successMessageFragment, viewModelProvider.get());
    }

    public void inject(VerificationPhoneErrorMessageFragment errorMessageFragment) {
        VerificationPhoneErrorMessageFragmentInjector.injectViewModelFactory(errorMessageFragment, viewModelProvider.get());
    }

    public void inject(VerificationBankFragment verificationBankFragment) {
        VerificationBankFragmentInjector.injectViewModelFactory(verificationBankFragment, viewModelProvider.get());
    }

    public void inject(VerificationBankExternalGatewayFragment verificationBankExternalGatewayFragment) {
        VerificationBankExternalGatewayFragmentInjector.injectViewModelFactory(verificationBankExternalGatewayFragment, viewModelProvider.get());
    }

    public void inject(VerificationBankSuccessMessageFragment verificationBankSuccessMessageFragment) {
        VerificationBankSuccessMessageFragmentInjector.injectViewModelFactory(verificationBankSuccessMessageFragment, viewModelProvider.get());
    }

    public void inject(VerificationBankErrorMessageFragment verificationBankErrorMessageFragment) {
        VerificationBankErrorMessageFragmentInjector.injectViewModelFactory(verificationBankErrorMessageFragment, viewModelProvider.get());
    }

    public void inject(ContractSigningFragment contractSigningFragment) {
        ContractSigningFragmentInjector.injectViewModelFactory(contractSigningFragment, viewModelProvider.get());
    }

    public void inject(ContractSigningPreviewFragment contractSigningPreviewFragment) {
        ContractSigningPreviewFragmentInjector.injectViewModelFactory(contractSigningPreviewFragment, viewModelProvider.get());
    }

    public void inject(IdentitySummaryFragment identitySummaryFragment) {

    }

    public void inject(IdentitySummaryActivity identitySummaryActivity) {

    }

    public void inject(ProgressIndicatorFragment progressIndicatorFragment) {
        ProgressIndicatorFragmentInjector.injectViewModelFactory(progressIndicatorFragment, viewModelProvider.get());
    }

    private static class Builder {
        private LibraryModule libraryModule;
        private IdentityModule identityModule;
        private IntroModule introModule;

        private Builder() {
        }

        Builder setLibraryModule(LibraryModule libraryModule) {
            this.libraryModule = libraryModule;
            return this;
        }

        Builder setIdentityModule(IdentityModule identityModule) {
            this.identityModule = identityModule;
            return this;
        }

        Builder setIntroModule(IntroModule introModule) {
            this.introModule = introModule;
            return this;
        }

        public LibraryComponent build() {
            return new LibraryComponent(identityModule, introModule, libraryModule);
        }
    }
}
