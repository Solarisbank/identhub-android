package de.solarisbank.identhub.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.base.BaseActivity;
import de.solarisbank.identhub.databinding.ActivityIntroBinding;
import de.solarisbank.identhub.di.ActivityComponent;
import de.solarisbank.identhub.identity.IdentityActivity;
import de.solarisbank.identhub.session.IdentHubSession;

public final class IntroActivity extends BaseActivity {

    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }
}