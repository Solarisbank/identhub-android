package de.solarisbank.identhub.verfication.bank.success;

import androidx.fragment.app.Fragment;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.data.entity.Status;
import de.solarisbank.identhub.di.FragmentComponent;
import de.solarisbank.identhub.success.SuccessMessageFragment;

import static de.solarisbank.identhub.router.RouterKt.STATUS_KEY;
import static de.solarisbank.identhub.session.IdentHub.LAST_COMPLETED_STEP_KEY;

public final class VerificationBankSuccessMessageFragment extends SuccessMessageFragment {

    public static Fragment newInstance() {
        return new VerificationBankSuccessMessageFragment();
    }

    @Override
    protected void initViews() {
        super.initViews();
        //todo refactor
        if (getArguments() != null
                && getArguments().getString(STATUS_KEY).equals(Status.IDENTIFICATION_DATA_REQUIRED.getLabel())
                || getArguments().getString(STATUS_KEY).equals(Status.AUTHORIZATION_REQUIRED.getLabel())
        ) {
//            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("identhub", Context.MODE_PRIVATE);
//            IdentificationStepPreferences identificationStepPreferences = new IdentificationStepPreferences(sharedPreferences);
//            identificationStepPreferences.save(IdentHubSession.Step.ON_PAYMENT_SUCCESS);
            getArguments().putInt(LAST_COMPLETED_STEP_KEY, 4);
            submitButton.setOnClickListener(view -> sharedViewModel.callOnPaymentResult(getArguments()));
        } else {
            submitButton.setOnClickListener(view -> sharedViewModel.postDynamicNavigationNextStep(getArguments()));
        }
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
