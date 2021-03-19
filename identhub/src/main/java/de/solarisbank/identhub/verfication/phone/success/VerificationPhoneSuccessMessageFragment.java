package de.solarisbank.identhub.verfication.phone.success;

import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.di.FragmentComponent;
import de.solarisbank.identhub.success.SuccessMessageFragment;

public class VerificationPhoneSuccessMessageFragment extends SuccessMessageFragment {

    @Override
    protected void initViewModel() {
        super.initViewModel();
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(VerificationPhoneSuccessViewModel.class);
    }

    @Override
    protected void initViews() {
        super.initViews();
        binding.submitButton.setOnClickListener(view -> sharedViewModel.navigateToIBanVerification());
    }

    @Override
    protected int getMessage() {
        return R.string.verification_phone_success_message;
    }

    @Override
    protected int getTitle() {
        return R.string.verification_phone_success_title;
    }

    @Override
    protected int getSubmitButtonLabel() {
        return R.string.verification_phone_action_next_step;
    }

    @Override
    protected void inject(FragmentComponent component) {
        component.inject(this);
    }
}
