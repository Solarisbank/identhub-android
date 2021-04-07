package de.solarisbank.identhub.intro;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.base.IdentHubActivity;
import de.solarisbank.identhub.di.ActivitySubcomponent;
import de.solarisbank.identhub.identity.IdentityActivity;
import de.solarisbank.identhub.session.IdentHubSession;

public final class IntroActivity extends IdentHubActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        IntroActivityViewModel viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(IntroActivityViewModel.class);

        IdentHubSession.Step lastCompletedStep = viewModel.getLastCompletedStep();

        if (lastCompletedStep == IdentHubSession.Step.VERIFICATION_BANK) {
            Intent intent = new Intent(this, IdentityActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();
            return;
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, IntroFragment.newInstance(), IntroFragment.TAG).commit();
        }
    }

    @Override
    protected void inject(ActivitySubcomponent activitySubcomponent) {
        activitySubcomponent.inject(this);
    }
}