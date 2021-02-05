package de.solarisbank.identhub.verfication.bank.error;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.R;
import de.solarisbank.identhub.di.LibraryComponent;
import de.solarisbank.identhub.error.ErrorMessageFragment;

public class VerificationBankErrorMessageFragment extends ErrorMessageFragment {

    ViewModelFactory viewModelFactory;

    public static Fragment newInstance() {
        return new VerificationBankErrorMessageFragment();
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(VerificationBankErrorViewModel.class);
    }

    @Override
    protected int getCancelButtonLabel() {
        return R.string.verification_phone_action_quit;
    }

    @Override
    protected int getMessage() {
        return R.string.verification_bank_error_message;
    }

    @Override
    protected int getTitle() {
        return R.string.verification_bank_error_title;
    }

    @Override
    protected int getSubmitButtonLabel() {
        return R.string.verification_bank_action_retry;
    }

    @Override
    protected void inject(LibraryComponent component) {
        component.inject(this);
    }
}
