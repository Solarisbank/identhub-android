package de.solarisbank.identhub.contract.sign;

import android.content.Intent;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.di.FragmentComponent;
import de.solarisbank.identhub.identity.summary.IdentitySummaryActivity;
import de.solarisbank.identhub.progress.ProgressIndicatorFragment;

public class ApplicationProcessingFragment extends ProgressIndicatorFragment {

    @Override
    protected int getTitleResource() {
        return R.string.progress_indicator_precessing_application_title;
    }

    @Override
    protected int getMessageResource() {
        return R.string.progress_indicator_precessing_application_message;
    }

    @Override
    protected void inject(FragmentComponent component) {
        component.inject(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        icon.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), IdentitySummaryActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }
}