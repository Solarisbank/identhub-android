package de.solarisbank.identhub.verfication.phone.error;

import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.R;
import de.solarisbank.identhub.di.LibraryComponent;
import de.solarisbank.identhub.error.ErrorMessageFragment;

public class VerificationPhoneErrorMessageFragment extends ErrorMessageFragment {

    ViewModelFactory viewModelFactory;

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(VerificationPhoneErrorViewModel.class);
    }

    @Override
    protected int getCancelButtonLabel() {
        return R.string.verification_phone_action_quit;
    }

    @Override
    protected int getMessage() {
        return R.string.verification_phone_error_default_message;
    }

    @Override
    protected int getTitle() {
        return R.string.verification_phone_error_title;
    }

    @Override
    protected int getSubmitButtonLabel() {
        return R.string.verification_phone_action_retry;
    }

    @Override
    protected void inject(LibraryComponent component) {
        component.inject(this);
    }
}
