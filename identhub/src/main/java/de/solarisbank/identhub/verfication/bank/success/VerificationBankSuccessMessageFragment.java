package de.solarisbank.identhub.verfication.bank.success;

import androidx.fragment.app.Fragment;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.di.FragmentComponent;
import de.solarisbank.identhub.success.SuccessMessageFragment;

public final class VerificationBankSuccessMessageFragment extends SuccessMessageFragment {

    public static Fragment newInstance() {
        return new VerificationBankSuccessMessageFragment();
    }

    @Override
    protected void initViews() {
        super.initViews();
        binding.submitButton.setOnClickListener(view -> sharedViewModel.navigateToContractSigningPreview());
    }

    @Override
    protected int getMessage() {
        return R.string.verification_bank_success_message;
    }

    @Override
    protected int getTitle() {
        return R.string.verification_bank_success_title;
    }

    @Override
    protected int getSubmitButtonLabel() {
        return R.string.verification_bank_action_next_step;
    }

    @Override
    protected void inject(FragmentComponent component) {
        component.inject(this);
    }
}
