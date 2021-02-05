package de.solarisbank.identhub.di;

import androidx.lifecycle.ViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

import de.solarisbank.identhub.di.internal.Provider;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModelFactory;
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel;
import de.solarisbank.identhub.contract.sign.ContractSigningViewModelFactory;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;
import de.solarisbank.identhub.identity.IdentityActivityViewModelFactory;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.intro.IntroActivityViewModel;
import de.solarisbank.identhub.intro.IntroActivityViewModelFactory;
import de.solarisbank.identhub.intro.IntroModule;
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModel;
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModelFactory;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorViewModel;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorViewModelFactory;
import de.solarisbank.identhub.verfication.bank.success.VerificationBankSuccessViewModel;
import de.solarisbank.identhub.verfication.bank.success.VerificationBankSuccessViewModelFactory;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModelFactory;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorViewModel;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorViewModelFactory;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModel;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModelFactory;

public class ViewModelMapProvider implements Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> {

    private final IdentityModule identityModule;
    private final IntroModule introModule;

    public ViewModelMapProvider(
            IdentityModule identityModule,
            IntroModule introModule) {
        this.identityModule = identityModule;
        this.introModule = introModule;
    }

    public static ViewModelMapProvider create(
            IdentityModule identityModule,
            IntroModule introModule
    ) {
        return new ViewModelMapProvider(identityModule, introModule);
    }

    @Override
    public Map<Class<? extends ViewModel>, Provider<ViewModel>> get() {
        Map<Class<? extends ViewModel>, Provider<ViewModel>> map = new LinkedHashMap<>(2);

        map.put(ContractSigningViewModel.class, ContractSigningViewModelFactory.create(identityModule));
        map.put(ContractSigningPreviewViewModel.class, ContractSigningPreviewViewModelFactory.create(identityModule));
        map.put(IdentityActivityViewModel.class, IdentityActivityViewModelFactory.create(identityModule));
        map.put(IntroActivityViewModel.class, IntroActivityViewModelFactory.create(introModule));
        map.put(VerificationPhoneViewModel.class, VerificationPhoneViewModelFactory.create(identityModule));
        map.put(VerificationPhoneSuccessViewModel.class, VerificationPhoneSuccessViewModelFactory.create(identityModule));
        map.put(VerificationPhoneErrorViewModel.class, VerificationPhoneErrorViewModelFactory.create(identityModule));
        map.put(VerificationBankViewModel.class, VerificationBankViewModelFactory.create(identityModule));
        map.put(VerificationBankErrorViewModel.class, VerificationBankErrorViewModelFactory.create(identityModule));
        map.put(VerificationBankSuccessViewModel.class, VerificationBankSuccessViewModelFactory.create(identityModule));

        return map;
    }
}
