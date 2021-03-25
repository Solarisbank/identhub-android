package de.solarisbank.identhub.di;

import de.solarisbank.identhub.contract.preview.ContractSigningPreviewFragment;
import de.solarisbank.identhub.contract.sign.ContractSigningFragment;
import de.solarisbank.identhub.fourthline.terms.TermsAndConditionsFragment;
import de.solarisbank.identhub.identity.summary.IdentitySummaryFragment;
import de.solarisbank.identhub.intro.IntroFragment;
import de.solarisbank.identhub.progress.ProgressIndicatorFragment;
import de.solarisbank.identhub.verfication.bank.VerificationBankFragment;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorMessageFragment;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragment;
import de.solarisbank.identhub.verfication.bank.success.VerificationBankSuccessMessageFragment;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneFragment;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorMessageFragment;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragment;

public interface FragmentComponent {

    void inject(VerificationPhoneFragment verificationPhoneFragment);

    void inject(VerificationPhoneSuccessMessageFragment successMessageFragment);

    void inject(VerificationPhoneErrorMessageFragment errorMessageFragment);

    void inject(VerificationBankFragment verificationBankFragment);

    void inject(VerificationBankExternalGatewayFragment verificationBankExternalGatewayFragment);

    void inject(VerificationBankSuccessMessageFragment verificationBankSuccessMessageFragment);

    void inject(VerificationBankErrorMessageFragment verificationBankErrorMessageFragment);

    void inject(ContractSigningFragment contractSigningFragment);

    void inject(ContractSigningPreviewFragment contractSigningPreviewFragment);

    void inject(IdentitySummaryFragment identitySummaryFragment);

    void inject(ProgressIndicatorFragment progressIndicatorFragment);

    void inject(IntroFragment introFragment);

    void inject(TermsAndConditionsFragment termsAndConditionsFragment);

    enum Initializer {
        ;

        public static FragmentComponent init(ActivityComponent activityComponent) {
            return activityComponent.plus();
        }
    }
}
