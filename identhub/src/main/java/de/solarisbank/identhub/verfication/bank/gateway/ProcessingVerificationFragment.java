package de.solarisbank.identhub.verfication.bank.gateway;

import androidx.navigation.fragment.NavHostFragment;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.di.LibraryComponent;
import de.solarisbank.identhub.progress.ProgressIndicatorFragment;

public class ProcessingVerificationFragment extends ProgressIndicatorFragment {

    @Override
    protected int getTitleResource() {
        return R.string.progress_indicator_precessing_verification_message;
    }

    @Override
    protected void inject(LibraryComponent component) {
        component.inject(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        binding.icon.setOnClickListener(view ->
                NavHostFragment.findNavController(ProcessingVerificationFragment.this)
                        .navigate(R.id.action_processingVerificationFragment_to_verificationBankSuccessMessageFragment));
    }
}