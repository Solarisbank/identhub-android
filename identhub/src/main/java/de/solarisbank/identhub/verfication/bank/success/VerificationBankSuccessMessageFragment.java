package de.solarisbank.identhub.verfication.bank.success;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.di.LibraryComponent;
import de.solarisbank.identhub.success.SuccessMessageFragment;

public class VerificationBankSuccessMessageFragment extends SuccessMessageFragment {

    public static Fragment newInstance() {
        return new VerificationBankSuccessMessageFragment();
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(VerificationBankSuccessViewModel.class);
    }

    @Override
    protected void initViews() {
        super.initViews();
        binding.submitButton.setOnClickListener(view -> sharedViewModel.navigateTo(R.id.action_verificationBankSuccessMessageFragment_to_contractSigningPreviewFragment));
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
    protected void inject(LibraryComponent component) {
        component.inject(this);
    }
}
