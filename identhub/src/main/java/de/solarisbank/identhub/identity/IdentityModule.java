package de.solarisbank.identhub.identity;

import androidx.annotation.NonNull;

import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel;
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel;
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModel;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorViewModel;
import de.solarisbank.identhub.verfication.bank.success.VerificationBankSuccessViewModel;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorViewModel;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModel;

public class IdentityModule {

    @NonNull
    public IdentityActivityViewModel provideIdentityActivityViewModel() {
        return new IdentityActivityViewModel();
    }

    @NonNull
    public VerificationPhoneViewModel provideVerificationPhoneViewModel() {
        return new VerificationPhoneViewModel();
    }

    @NonNull
    public VerificationPhoneSuccessViewModel provideVerificationPhoneSuccessViewModel() {
        return new VerificationPhoneSuccessViewModel();
    }

    @NonNull
    public VerificationPhoneErrorViewModel provideVerificationPhoneErrorViewModel() {
        return new VerificationPhoneErrorViewModel();
    }

    @NonNull
    public VerificationBankViewModel provideVerificationBankViewModel() {
        return new VerificationBankViewModel();
    }

    @NonNull
    public VerificationBankSuccessViewModel provideVerificationBankSuccessViewModel() {
        return new VerificationBankSuccessViewModel();
    }

    @NonNull
    public VerificationBankErrorViewModel provideVerificationBankErrorViewModel() {
        return new VerificationBankErrorViewModel();
    }

    @NonNull
    public ContractSigningViewModel provideContractSigningViewModel() {
        return new ContractSigningViewModel();
    }

    @NonNull
    public ContractSigningPreviewViewModel provideContractSigningPreviewViewModel() {
        return new ContractSigningPreviewViewModel();
    }
}
