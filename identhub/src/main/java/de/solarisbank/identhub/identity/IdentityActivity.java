package de.solarisbank.identhub.identity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.base.BaseActivity;
import de.solarisbank.identhub.databinding.ActivityIdentityBinding;
import de.solarisbank.identhub.di.ActivityComponent;
import de.solarisbank.identhub.identity.summary.IdentitySummaryActivity;
import de.solarisbank.identhub.navigation.NaviDirection;
import de.solarisbank.identhub.session.IdentHubSession;
import de.solarisbank.identhub.ui.StepIndicatorView;
import de.solarisbank.shared.result.Event;

public final class IdentityActivity extends BaseActivity {

    private ActivityIdentityBinding binding;

    private IdentityActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIdentityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initGraph();
        initView();
    }

    public void initGraph() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavInflater navInflater = navHostFragment.getNavController().getNavInflater();
        NavGraph navGraph = navInflater.inflate(R.navigation.identity_nav_graph);

        IdentHubSession.Step lastCompletedStep = viewModel.getLastCompletedStep();
        if (lastCompletedStep == IdentHubSession.Step.VERIFICATION_BANK) {
            navGraph.setStartDestination(R.id.contractSigningPreviewFragment);
        }
        navHostFragment.getNavController().setGraph(navGraph, getIntent().getExtras());
    }

    private void initView() {
        IdentHubSession.Step lastCompletedStep = viewModel.getLastCompletedStep();
        binding.stepIndicator.setStep(lastCompletedStep != null ? lastCompletedStep.getIndex() : IdentHubSession.Step.VERIFICATION_PHONE.getIndex());
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();

        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(IdentityActivityViewModel.class);

        viewModel.getNaviDirectionEvent().observe(this, this::onNavigationChanged);

    }

    private void onNavigationChanged(@NonNull Event<NaviDirection> event) {
        NaviDirection naviDirection = event.getContent();

        if (naviDirection != null) {
            viewModel.doOnNavigationChanged(naviDirection.getActionId());
            int naviActionId = naviDirection.getActionId();

            if (naviActionId == IdentityActivityViewModel.ACTION_SUMMARY_WITH_RESULT) {
                startSummaryActivity();
            } else if (naviActionId != IdentityActivityViewModel.ACTION_QUIT &&
                    naviActionId != IdentityActivityViewModel.ACTION_STOP_WITH_RESULT) {
                Navigation.findNavController(binding.navHostFragment).navigate(naviActionId, naviDirection.getArgs());
            } else {
                quit(naviDirection.getArgs());
                return;
            }

            if (naviDirection.getActionId() == R.id.action_verificationPhoneSuccessMessageFragment_to_verificationBankFragment) {
                binding.stepIndicator.setStep(StepIndicatorView.SECOND_STEP);
            } else if (naviDirection.getActionId() == R.id.action_verificationBankSuccessMessageFragment_to_contractSigningPreviewFragment) {
                binding.stepIndicator.setStep(StepIndicatorView.THIRD_STEP);
            }
        }
    }

    private void startSummaryActivity() {
        Intent intent = new Intent(this, IdentitySummaryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
    }

    private void quit(Bundle bundle) {
        Intent intent = null;
        if (bundle != null) {
            intent = new Intent();
            intent.putExtras(bundle);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}